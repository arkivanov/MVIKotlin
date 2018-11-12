package com.arkivanov.mvidroid.store.timetravel

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.MviEventType
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.utils.Disposables
import com.arkivanov.mvidroid.utils.assertOnMainThread
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

internal class MviTimeTravelStore<out State : Any, in Intent : Any, out Label : Any, Action : Any, Result : Any> @MainThread constructor(
    private val name: String,
    initialState: State,
    private val bootstrapper: MviBootstrapper<Action>?,
    private val intentToAction: (Intent) -> Action,
    private val executorFactory: () -> MviExecutor<State, Action, Result, Label>,
    private val reducer: MviReducer<State, Result>
) : MviStore<State, Intent, Label> {
    private val executor = executorFactory()
    private val disposables = Disposables()
    private val statesSubject = BehaviorSubject.createDefault(initialState)
    private val labelsSubject = PublishSubject.create<Label>()
    private val eventsSubject = PublishSubject.create<MviTimeTravelEvent>()
    private var internalState = initialState
    override val states: Observable<out State> = statesSubject

    override val state: State
        get() {
            assertOnMainThread()
            return statesSubject.value
        }

    override val labels: Observable<out Label> = labelsSubject
    val events: Observable<MviTimeTravelEvent> = eventsSubject
    val eventProcessor = EventProcessor()
    val eventDebugger = EventDebugger()

    init {
        assertOnMainThread()
    }

    override fun accept(intent: Intent) {
        onEvent(MviEventType.INTENT, intent)
    }

    override fun dispose() {
        assertOnMainThread()
        disposables.dispose()
        statesSubject.onComplete()
        labelsSubject.onComplete()
        eventsSubject.onComplete()
    }

    override fun isDisposed(): Boolean {
        assertOnMainThread()
        return disposables.isDisposed
    }

    fun init() {
        executor.init(
            { internalState },
            { onEvent(MviEventType.RESULT, it) },
            { onEvent(MviEventType.LABEL, it) }
        )

        bootstrapper
            ?.bootstrap { onEvent(MviEventType.ACTION, it) }
            ?.also(disposables::add)
    }

    fun restoreState() {
        statesSubject.onNext(internalState)
    }

    private fun onEvent(type: MviEventType, value: Any, state: State? = null) {
        assertOnMainThread()
        eventsSubject.onNext(MviTimeTravelEvent(name, type, value, state ?: this.state))
    }

    inner class EventProcessor {
        @Suppress("UNCHECKED_CAST")
        fun process(type: MviEventType, value: Any) {
            when (type) {
                MviEventType.INTENT -> processIntent(value as Intent)
                MviEventType.ACTION -> processAction(value as Action)
                MviEventType.RESULT -> processResult(value as Result)
                MviEventType.STATE -> processState(value as State)
                MviEventType.LABEL -> processLabel(value as Label)
            }
        }

        private fun processIntent(intent: Intent) {
            onEvent(MviEventType.ACTION, intentToAction(intent))
        }

        private fun processAction(action: Action) {
            executor.execute(action)?.also(disposables::add)
        }

        private fun processResult(result: Result) {
            val previousState = internalState
            with(reducer) {
                internalState = internalState.reduce(result)
            }
            onEvent(MviEventType.STATE, internalState, previousState)
        }

        private fun processState(state: State) {
            statesSubject.onNext(state)
        }

        private fun processLabel(label: Label) {
            labelsSubject.onNext(label)
        }

    }

    inner class EventDebugger {
        @Suppress("UNCHECKED_CAST")
        @MainThread
        fun debug(event: MviTimeTravelEvent) {
            assertOnMainThread()

            when (event.type) {
                MviEventType.INTENT -> debugIntent(event.value as Intent)
                MviEventType.ACTION -> debugAction(event.value as Action, event.state as State)
                MviEventType.RESULT -> debugResult(event.value as Result, event.state as State)
                MviEventType.STATE -> throw IllegalArgumentException("Can't debug event: $event")
                MviEventType.LABEL -> debugLabel(event.value as Label)
            }
        }

        private fun debugIntent(intent: Intent) {
            intentToAction(intent)
        }

        private fun debugAction(action: Action, initialState: State) {
            var localState: State = initialState
            executorFactory()
                .apply {
                    val s = this
                    s.toString()
                    init(
                        { localState },
                        {
                            assertOnMainThread()
                            with(reducer) {
                                localState = localState.reduce(it)
                            }
                        },
                        { assertOnMainThread() }
                    )
                }
                .execute(action)
                ?.also(disposables::add)
        }

        private fun debugResult(result: Result, initialState: State) {
            with(reducer) {
                initialState.reduce(result)
            }
        }

        private fun debugLabel(label: Label) {
            labelsSubject.onNext(label)
        }
    }
}
