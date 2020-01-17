package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.internal.rx.Subject
import com.arkivanov.mvikotlin.core.internal.rx.isActive
import com.arkivanov.mvikotlin.core.internal.rx.onComplete
import com.arkivanov.mvikotlin.core.internal.rx.onNext
import com.arkivanov.mvikotlin.core.internal.rx.subscribe
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.atomic.updateAndGet

internal class TimeTravelStore<in Intent, in Action, out Result, out State, out Label> @MainThread constructor(
    private val name: String,
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
    private val reducer: Reducer<State, Result>
) : Store<Intent, State, Label> {

    init {
        assertOnMainThread()
    }

    private val executor = executorFactory()
    private val _state = AtomicReference(initialState)
    override val state: State get() = _state.value
    private val internalState = AtomicReference(initialState)
    private val stateSubject = Subject<State>()
    private val labelSubject = Subject<Label>()
    private val eventSubject = Subject<TimeTravelEvent>()
    override val isDisposed: Boolean get() = !stateSubject.isActive
    private val debuggingExecutor = AtomicReference<Executor<*, *, *, *, *>?>(null)

    val eventProcessor = EventProcessor()
    val eventDebugger = EventDebugger()

    override fun states(observer: Observer<State>): Disposable {
        assertOnMainThread()

        return stateSubject.subscribe(observer, _state.value)
    }

    override fun labels(observer: Observer<Label>): Disposable {
        assertOnMainThread()

        return labelSubject.subscribe(observer)
    }

    @MainThread
    fun events(observer: Observer<TimeTravelEvent>): Disposable {
        assertOnMainThread()

        return eventSubject.subscribe(observer)
    }

    override fun accept(intent: Intent) {
        assertOnMainThread()

        doIfNotDisposed {
            onEvent(StoreEventType.INTENT, intent, state)
        }
    }

    override fun dispose() {
        assertOnMainThread()

        cancelDebugging()

        doIfNotDisposed {
            bootstrapper?.dispose()
            executor.dispose()
            stateSubject.onComplete()
            labelSubject.onComplete()
            eventSubject.onComplete()
        }
    }

    @MainThread
    fun init() {
        assertOnMainThread()

        executor.init(
            stateSupplier = internalState::value,
            resultConsumer = { onEvent(StoreEventType.RESULT, it, state) },
            labelConsumer = { onEvent(StoreEventType.LABEL, it, state) }
        )

        bootstrapper?.bootstrap {
            onEvent(StoreEventType.ACTION, it, state)
        }
    }

    @MainThread
    fun restoreState() {
        assertOnMainThread()

        doIfNotDisposed {
            changeState(internalState.value)
        }
    }

    @MainThread
    fun cancelDebugging() {
        assertOnMainThread()

        doIfNotDisposed {
            debuggingExecutor
                .getAndSet(null)
                ?.dispose()
        }
    }

    private fun onEvent(type: StoreEventType, value: Any?, state: State) {
        assertOnMainThread()

        doIfNotDisposed {
            eventSubject.onNext(TimeTravelEvent(name, type, value, state))
        }
    }

    private fun changeState(state: State) {
        _state.value = state
        stateSubject.onNext(state)
    }

    private inline fun doIfNotDisposed(block: () -> Unit) {
        if (!isDisposed) {
            block()
        }
    }

    inner class EventProcessor {
        @Suppress("UNCHECKED_CAST")
        @MainThread
        fun process(type: StoreEventType, value: Any?) {
            assertOnMainThread()

            doIfNotDisposed {
                when (type) {
                    StoreEventType.INTENT -> executor.handleIntent(value as Intent)
                    StoreEventType.ACTION -> executor.handleAction(value as Action)
                    StoreEventType.RESULT -> processResult(value as Result)
                    StoreEventType.STATE -> changeState(value as State)
                    StoreEventType.LABEL -> labelSubject.onNext(value as Label)
                }.let {}
            }
        }

        private fun processResult(result: Result) {
            val previousState = internalState.value

            val newState =
                internalState.updateAndGet {
                    reducer.run { it.reduce(result) }
                }

            onEvent(StoreEventType.STATE, newState, previousState)
        }
    }

    inner class EventDebugger {
        @Suppress("UNCHECKED_CAST")
        @MainThread
        fun debug(event: TimeTravelEvent) {
            assertOnMainThread()

            doIfNotDisposed {
                when (event.type) {
                    StoreEventType.INTENT -> debugIntent(event.value as Intent, event.state as State)
                    StoreEventType.ACTION -> debugAction(event.value as Action, event.state as State)
                    StoreEventType.RESULT -> debugResult(event.value as Result, event.state as State)
                    StoreEventType.STATE -> throw IllegalArgumentException("Can't debug event: $event")
                    StoreEventType.LABEL -> debugLabel(event.value as Label)
                }.let {}
            }
        }

        private fun debugIntent(intent: Intent, initialState: State) {
            debugExecutor(initialState) {
                handleIntent(intent)
            }
        }

        private fun debugAction(action: Action, initialState: State) {
            debugExecutor(initialState) {
                handleAction(action)
            }
        }

        private fun debugExecutor(initialState: State, execute: Executor<Intent, Action, State, Result, Label>.() -> Unit) {
            val localState = AtomicReference(initialState)

            val executor =
                executorFactory().apply {
                    init(
                        { localState.value },
                        { result ->
                            assertOnMainThread()
                            reducer.run {
                                localState.update { it.reduce(result) }
                            }
                        },
                        { assertOnMainThread() }
                    )

                    execute()
                }

            debuggingExecutor
                .getAndSet(executor)
                ?.dispose()
        }

        private fun debugResult(result: Result, initialState: State) {
            with(reducer) {
                initialState.reduce(result)
            }
        }

        private fun debugLabel(label: Label) {
            labelSubject.onNext(label)
        }
    }
}
