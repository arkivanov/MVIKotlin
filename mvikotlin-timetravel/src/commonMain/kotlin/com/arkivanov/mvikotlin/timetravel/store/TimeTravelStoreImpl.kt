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
import com.arkivanov.mvikotlin.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.Event
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.atomic.updateAndGet

internal class TimeTravelStoreImpl<in Intent : Any, in Action : Any, in Result : Any, out State : Any, Label : Any> @MainThread constructor(
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
    private val reducer: Reducer<State, Result>
) : TimeTravelStore<Intent, State, Label> {

    init {
        assertOnMainThread()
    }

    private val executor = executorFactory()
    private val internalState = AtomicReference(initialState)
    private val stateSubject = BehaviorSubject(initialState)
    override val state: State get() = stateSubject.value
    override val isDisposed: Boolean get() = !stateSubject.isActive
    private val labelSubject = PublishSubject<Label>()
    private val eventSubject = PublishSubject<Event>()
    private val debuggingExecutor = AtomicReference<Executor<*, *, *, *, *>?>(null)
    private val eventProcessor = EventProcessor()
    private val eventDebugger = EventDebugger()

    override fun states(observer: Observer<State>): Disposable {
        assertOnMainThread()

        return stateSubject.subscribe(observer)
    }

    override fun labels(observer: Observer<Label>): Disposable {
        assertOnMainThread()

        return labelSubject.subscribe(observer)
    }

    override fun events(observer: Observer<Event>): Disposable {
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

        bootstrapper?.init {
            onEvent(StoreEventType.ACTION, it, state)
        }

        bootstrapper?.invoke()
    }

    override fun restoreState() {
        assertOnMainThread()

        doIfNotDisposed {
            changeState(internalState.value)
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
            eventSubject.onNext(Event(type = type, value = value, state = state))
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
