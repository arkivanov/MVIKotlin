package com.arkivanov.mvikotlin.main.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.internal.BehaviorSubject
import com.arkivanov.mvikotlin.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.rx.observer

internal class DefaultStore<in Intent : Any, in Action : Any, in Message : Any, out State : Any, Label : Any>(
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executor: Executor<Intent, Action, State, Message, Label>,
    private val reducer: Reducer<State, Message>
) : Store<Intent, State, Label> {

    private val intentSubject = PublishSubject<Intent>()
    private val stateSubject = BehaviorSubject(initialState)
    override val state: State get() = stateSubject.value
    override val isDisposed: Boolean get() = !stateSubject.isActive
    private val labelSubject = PublishSubject<Label>()

    override fun init() {
        assertOnMainThread()

        intentSubject.subscribe(observer(onNext = ::onIntent))

        executor.init(
            object : Executor.Callbacks<State, Message, Label> {
                override val state: State get() = stateSubject.value

                override fun onMessage(message: Message) {
                    assertOnMainThread()

                    doIfNotDisposed {
                        changeState { oldState ->
                            reducer.run { oldState.reduce(message) }
                        }
                    }
                }

                override fun onLabel(label: Label) {
                    assertOnMainThread()

                    labelSubject.onNext(label)
                }
            }
        )

        bootstrapper?.init { action ->
            assertOnMainThread()

            doIfNotDisposed {
                executor.executeAction(action)
            }
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
