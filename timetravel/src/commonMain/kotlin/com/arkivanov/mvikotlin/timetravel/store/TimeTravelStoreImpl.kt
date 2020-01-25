package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
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
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.EventDebugger
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.EventProcessor
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.atomic.updateAndGet

internal class TimeTravelStoreImpl<in Intent : Any, in Action : Any, in Result : Any, out State : Any, Label : Any> @MainThread constructor(
    override val name: String,
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
    private val reducer: Reducer<State, Result>
) : TimeTravelStore<Intent, State, Label> {

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

    override val eventProcessor: EventProcessor = EventProcessorImpl()
    override val eventDebugger: EventDebugger = EventDebuggerImpl()

    override fun states(observer: Observer<State>): Disposable {
        assertOnMainThread()

        return stateSubject.subscribe(observer, _state.value)
    }

    override fun labels(observer: Observer<Label>): Disposable {
        assertOnMainThread()

        return labelSubject.subscribe(observer)
    }

    override fun events(observer: Observer<TimeTravelEvent>): Disposable {
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

        doIfNotDisposed {
            debuggingExecutor.getAndSet(null)?.dispose()
            bootstrapper?.dispose()
            executor.dispose()
            stateSubject.onComplete()
            labelSubject.onComplete()
            eventSubject.onComplete()
        }
    }

    override fun init() {
        assertOnMainThread()

        executor.init(
            object : Executor.Callbacks<State, Result, Label> {
                override val state: State get() = internalState.value

                override fun onResult(result: Result) {
                    onEvent(StoreEventType.RESULT, result, state)
                }

                override fun onLabel(label: Label) {
                    onEvent(StoreEventType.LABEL, label, state)
                }
            }
        )

        bootstrapper?.bootstrap {
            onEvent(StoreEventType.ACTION, it, state)
        }
    }

    override fun restoreState() {
        assertOnMainThread()

        doIfNotDisposed {
            changeState(internalState.value)
        }
    }

    private fun onEvent(type: StoreEventType, value: Any, state: State) {
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

    private inner class EventProcessorImpl : EventProcessor {
        @Suppress("UNCHECKED_CAST")
        override fun process(type: StoreEventType, value: Any) {
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

    private inner class EventDebuggerImpl : EventDebugger {
        @Suppress("UNCHECKED_CAST")
        override fun debug(event: TimeTravelEvent) {
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
                        object : Executor.Callbacks<State, Result, Label> {
                            override val state: State get() = localState.value

                            override fun onResult(result: Result) {
                                assertOnMainThread()

                                reducer.run {
                                    localState.update { it.reduce(result) }
                                }
                            }

                            override fun onLabel(label: Label) {
                                assertOnMainThread()
                            }
                        }
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
