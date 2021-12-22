package com.arkivanov.mvikotlin.main.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
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

internal class DefaultStore<in Intent : Any, in Action : Any, in Result : Any, out State : Any, Label : Any> @MainThread constructor(
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    private val executor: Executor<Intent, Action, State, Result, Label>,
    private val reducer: Reducer<State, Result>
) : Store<Intent, State, Label> {

    private val intentSubject = PublishSubject<Intent>()
    private val stateSubject = BehaviorSubject(initialState)
    override val state: State get() = stateSubject.value
    override val isDisposed: Boolean get() = !stateSubject.isActive
    private val labelSubject = PublishSubject<Label>()

    private val getState: () -> State =
        {
            assertOnMainThread()
            stateSubject.value
        }

    override fun init() {
        assertOnMainThread()

        intentSubject.subscribe(observer(onNext = ::onIntent))

        executor.init(
            object : Executor.Callbacks<State, Result, Label> {
                override fun onResult(result: Result) {
                    assertOnMainThread()

                    doIfNotDisposed {
                        changeState { oldState ->
                            reducer.run { oldState.reduce(result) }
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
                executor.executeAction(action, getState)
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
            executor.executeIntent(intent, getState)
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
