package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.internal.BehaviorSubject
import com.arkivanov.mvikotlin.rx.internal.Disposable
import com.arkivanov.mvikotlin.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.Event
import com.arkivanov.mvikotlin.utils.internal.AtomicRef
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue

internal class TimeTravelStoreImpl<in Intent : Any, in Action : Any, in Result : Any, out State : Any, Label : Any> @MainThread constructor(
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
    private val reducer: Reducer<State, Result>
) : TimeTravelStore<Intent, State, Label> {

    private val executor = executorFactory()
    private var internalState by atomic(initialState)
    private val stateSubject = BehaviorSubject(initialState)
    override val state: State get() = stateSubject.value
    override val isDisposed: Boolean get() = !stateSubject.isActive
    private val labelSubject = PublishSubject<Label>()
    private val eventSubjects = StoreEventType.values().associateWith { PublishSubject<Event>() }
    private var debuggingExecutor by atomic<Executor<*, *, *, *, *>?>(null)
    private val eventProcessor = EventProcessor()
    private val eventDebugger = EventDebugger()

    private val getState: () -> State =
        {
            assertOnMainThread()
            internalState
        }


    override fun states(observer: Observer<State>): Disposable =
        stateSubject.subscribe(observer)

    override fun labels(observer: Observer<Label>): Disposable = labelSubject.subscribe(observer)

    override fun events(observer: Observer<Event>): Disposable {
        val disposables = eventSubjects.values.map { it.subscribe(observer) }

        return Disposable { disposables.forEach(Disposable::dispose) }
    }

    override fun accept(intent: Intent) {
        assertOnMainThread()

        doIfNotDisposed {
            onEvent(StoreEventType.INTENT, intent, state)
        }
    }

    override fun dispose() {
        assertOnMainThread()

        doIfNotDisposed {
            debuggingExecutor?.dispose()
            debuggingExecutor = null
            bootstrapper?.dispose()
            executor.dispose()
            stateSubject.onComplete()
            labelSubject.onComplete()
            eventSubjects.values.forEach(PublishSubject<*>::onComplete)
        }
    }

    override fun init() {
        assertOnMainThread()

        executor.init(
            object : Executor.Callbacks<State, Result, Label> {
                override fun onResult(result: Result) {
                    onEvent(StoreEventType.RESULT, result, state)
                }

                override fun onLabel(label: Label) {
                    onEvent(StoreEventType.LABEL, label, state)
                }
            }
        )

        bootstrapper?.init {
            onEvent(StoreEventType.ACTION, it, state)
        }

        bootstrapper?.invoke()
    }

    override fun restoreState() {
        assertOnMainThread()

        doIfNotDisposed {
            changeState(internalState)
        }
    }

    override fun process(type: StoreEventType, value: Any) {
        eventProcessor.process(type, value)
    }

    override fun debug(type: StoreEventType, value: Any, state: Any) {
        eventDebugger.debug(type, value, state)
    }

    private fun onEvent(type: StoreEventType, value: Any, state: State) {
        assertOnMainThread()

        doIfNotDisposed {
            eventSubjects.getValue(type).onNext(Event(type = type, value = value, state = state))
        }
    }

    private fun changeState(state: State) {
        stateSubject.onNext(state)
    }

    private inline fun doIfNotDisposed(block: () -> Unit) {
        if (!isDisposed) {
            block()
        }
    }

    private inner class EventProcessor {
        @Suppress("UNCHECKED_CAST")
        fun process(type: StoreEventType, value: Any) {
            assertOnMainThread()

            doIfNotDisposed {
                when (type) {
                    StoreEventType.INTENT -> executor.executeIntent(value as Intent, getState)
                    StoreEventType.ACTION -> executor.executeAction(value as Action, getState)
                    StoreEventType.RESULT -> processResult(value as Result)
                    StoreEventType.STATE -> changeState(value as State)
                    StoreEventType.LABEL -> labelSubject.onNext(value as Label)
                }.let {}
            }
        }

        private fun processResult(result: Result) {
            val previousState = internalState
            val newState = reducer.run { previousState.reduce(result) }
            internalState = newState

            onEvent(StoreEventType.STATE, newState, previousState)
        }
    }

    private inner class EventDebugger {
        @Suppress("UNCHECKED_CAST")
        fun debug(type: StoreEventType, value: Any, state: Any) {
            assertOnMainThread()

            doIfNotDisposed {
                when (type) {
                    StoreEventType.INTENT -> debugIntent(value as Intent, state as State)
                    StoreEventType.ACTION -> debugAction(value as Action, state as State)
                    StoreEventType.RESULT -> debugResult(value as Result, state as State)
                    StoreEventType.STATE -> throw IllegalArgumentException("Can't debug event of type: $type")
                    StoreEventType.LABEL -> debugLabel(value as Label)
                }.let {}
            }
        }

        private fun debugIntent(intent: Intent, initialState: State) {
            val stateRef = atomic(initialState)
            debugExecutor(stateRef) {
                executeIntent(intent = intent, getState = { stateRef.value })
            }
        }

        private fun debugAction(action: Action, initialState: State) {
            val stateRef = atomic(initialState)
            debugExecutor(stateRef) {
                executeAction(action = action, getState = { stateRef.value })
            }
        }

        private fun debugExecutor(stateRef: AtomicRef<State>, execute: Executor<Intent, Action, State, Result, Label>.() -> Unit) {
            val executor =
                executorFactory().apply {
                    init(
                        object : Executor.Callbacks<State, Result, Label> {
                            override fun onResult(result: Result) {
                                assertOnMainThread()

                                stateRef.value = reducer.run { stateRef.value.reduce(result) }
                            }

                            override fun onLabel(label: Label) {
                                assertOnMainThread()
                            }
                        }
                    )

                    execute()
                }

            debuggingExecutor?.dispose()
            debuggingExecutor = executor
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
