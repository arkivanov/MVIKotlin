package com.arkivanov.mvikotlin.main.store

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.rx.internal.BehaviorSubject
import com.arkivanov.mvikotlin.core.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread

internal class DefaultStore<in Intent : Any, in Action : Any, in Message : Any, out State : Any, Label : Any>(
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executor: Executor<Intent, Action, State, Message, Label>,
    private val reducer: Reducer<State, Message>
) : Store<Intent, State, Label> {

    private val intentSubject = PublishSubject<Intent>()
    private val actionSubject = PublishSubject<Action>()
    private val stateSubject = BehaviorSubject(initialState)
    override val state: State get() = stateSubject.value
    override val isDisposed: Boolean get() = !stateSubject.isActive
    private val labelSubject = PublishSubject<Label>()
    private var isInitialized = false

    override fun init() {
        assertOnMainThread()

        if (isInitialized) {
            return
        }

        isInitialized = true

        intentSubject.subscribe(observer(onNext = ::onIntent))
        actionSubject.subscribe(observer(onNext = ::onAction))

        executor.init(
            object : Executor.Callbacks<State, Message, Action, Label> {
                override val state: State get() = stateSubject.value

                override fun onMessage(message: Message) {
                    assertOnMainThread()

                    doIfNotDisposed {
                        changeState { oldState ->
                            reducer.run { oldState.reduce(message) }
                        }
                    }
                }

                @OptIn(ExperimentalMviKotlinApi::class)
                override fun onAction(action: Action) {
                    assertOnMainThread()
                    actionSubject.onNext(action)
                }

                override fun onLabel(label: Label) {
                    assertOnMainThread()
                    labelSubject.onNext(label)
                }
            }
        )

        bootstrapper?.init { action ->
            assertOnMainThread()
            actionSubject.onNext(action)
        }

        bootstrapper?.invoke()
    }

    private inline fun changeState(func: (State) -> State) {
        stateSubject.onNext(func(stateSubject.value))
    }

    override fun states(observer: Observer<State>): Disposable =
        stateSubject.subscribe(observer)

    override fun labels(observer: Observer<Label>): Disposable =
        labelSubject.subscribe(observer)

    override fun accept(intent: Intent) {
        assertOnMainThread()

        intentSubject.onNext(intent)
    }

    private fun onIntent(intent: Intent) {
        doIfNotDisposed {
            executor.executeIntent(intent)
        }
    }

    private fun onAction(action: Action) {
        doIfNotDisposed {
            executor.executeAction(action)
        }
    }

    override fun dispose() {
        assertOnMainThread()

        doIfNotDisposed {
            bootstrapper?.dispose()
            executor.dispose()
            intentSubject.onComplete()
            stateSubject.onComplete()
            labelSubject.onComplete()
        }
    }

    private inline fun doIfNotDisposed(block: () -> Unit) {
        if (!isDisposed) {
            block()
        }
    }
}
