package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.rx.internal.BehaviorSubject
import com.arkivanov.mvikotlin.core.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.store.StoreSerializers
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.Event
import kotlinx.serialization.KSerializer
import kotlin.concurrent.Volatile

internal class TimeTravelStoreImpl<in Intent : Any, in Action : Any, in Message : Any, State : Any, Label : Any>(
    initialState: State,
    private val serializers: StoreSerializers<Intent, Action, Message, State, Label>,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
    private val reducer: Reducer<State, Message>,
    private val onInit: (TimeTravelStore<Intent, State, Label>) -> Unit = {},
) : TimeTravelStore<Intent, State, Label> {

    private val executor = executorFactory()
    private var internalState = initialState
    private val stateSubject = BehaviorSubject(initialState)
    override val state: State get() = stateSubject.value
    override val isDisposed: Boolean get() = !stateSubject.isActive
    private val labelSubject = PublishSubject<Label>()
    private val eventSubjects = StoreEventType.entries.associateWith { PublishSubject<Event<*, State>>() }
    private var debuggingExecutor: Executor<*, *, *, *, *>? = null
    private val eventProcessor = EventProcessor()
    private val eventDebugger = EventDebugger()
    private var isInitialized = false

    override fun states(observer: Observer<State>): Disposable =
        stateSubject.subscribe(observer)

    override fun labels(observer: Observer<Label>): Disposable =
        labelSubject.subscribe(observer)

    override fun events(observer: Observer<Event<*, State>>): Disposable {
        val disposables = eventSubjects.values.map { it.subscribe(observer) }

        return Disposable { disposables.forEach(Disposable::dispose) }
    }

    override fun accept(intent: Intent) {
        assertOnMainThread()

        doIfNotDisposed {
            onEvent(StoreEventType.INTENT, intent, serializers.intentSerializer, state)
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

        if (isInitialized) {
            return
        }

        isInitialized = true

        onInit(this)

        executor.init(
            object : Executor.Callbacks<State, Message, Action, Label> {
                override val state: State get() = internalState

                override fun onMessage(message: Message) {
                    onEvent(StoreEventType.MESSAGE, message, serializers.messageSerializer, state)
                }

                @OptIn(ExperimentalMviKotlinApi::class)
                override fun onAction(action: Action) {
                    onEvent(StoreEventType.ACTION, action, serializers.actionSerializer, state)
                }

                override fun onLabel(label: Label) {
                    onEvent(StoreEventType.LABEL, label, serializers.labelSerializer, state)
                }
            }
        )

        bootstrapper?.init {
            onEvent(StoreEventType.ACTION, it, serializers.actionSerializer, state)
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

    private fun <T : Any> onEvent(type: StoreEventType, value: T, serializer: KSerializer<T>, state: State) {
        assertOnMainThread()

        doIfNotDisposed {
            eventSubjects.getValue(type).onNext(
                Event(
                    type = type,
                    value = value,
                    valueSerializer = serializer,
                    state = state,
                    stateSerializer = serializers.stateSerializer,
                )
            )
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
                    StoreEventType.INTENT -> executor.executeIntent(value as Intent)
                    StoreEventType.ACTION -> executor.executeAction(value as Action)
                    StoreEventType.MESSAGE -> processMessage(value as Message)
                    StoreEventType.STATE -> changeState(value as State)
                    StoreEventType.LABEL -> labelSubject.onNext(value as Label)
                }.let {}
            }
        }

        private fun processMessage(message: Message) {
            val previousState = internalState
            val newState = reducer.run { previousState.reduce(message) }
            internalState = newState

            onEvent(StoreEventType.STATE, newState, serializers.stateSerializer, previousState)
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
                    StoreEventType.MESSAGE -> debugMessage(value as Message, state as State)
                    StoreEventType.STATE -> throw IllegalArgumentException("Can't debug event of type: $type")
                    StoreEventType.LABEL -> debugLabel(value as Label)
                }.let {}
            }
        }

        private fun debugIntent(intent: Intent, initialState: State) {
            debugExecutor(initialState) {
                executeIntent(intent)
            }
        }

        private fun debugAction(action: Action, initialState: State) {
            debugExecutor(initialState) {
                executeAction(action)
            }
        }

        private fun debugExecutor(initialState: State, execute: Executor<Intent, Action, State, Message, Label>.() -> Unit) {
            val executor =
                executorFactory().apply {
                    init(DebugExecutorCallbacks(initialState, reducer))
                    execute()
                }

            debuggingExecutor?.dispose()
            debuggingExecutor = executor
        }

        private fun debugMessage(message: Message, initialState: State) {
            with(reducer) {
                initialState.reduce(message)
            }
        }

        private fun debugLabel(label: Label) {
            labelSubject.onNext(label)
        }
    }


    private class DebugExecutorCallbacks<State, in Message, in Action, in Label>(
        initialState: State,
        private val reducer: Reducer<State, Message>,
    ) : Executor.Callbacks<State, Message, Action, Label> {
        @Volatile
        override var state: State = initialState
            private set

        override fun onMessage(message: Message) {
            assertOnMainThread()
            state = reducer.run { state.reduce(message) }
        }

        @OptIn(ExperimentalMviKotlinApi::class)
        override fun onAction(action: Action) {
            assertOnMainThread()
        }

        override fun onLabel(label: Label) {
            assertOnMainThread()
        }
    }
}
