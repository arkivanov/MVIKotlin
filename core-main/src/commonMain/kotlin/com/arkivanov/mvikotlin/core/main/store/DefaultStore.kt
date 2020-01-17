package com.arkivanov.mvikotlin.core.main.store

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
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.updateAndGet

internal class DefaultStore<in Intent, in Action, out Result, out State, out Label> @MainThread constructor(
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
    private val reducer: Reducer<State, Result>
) : Store<Intent, State, Label> {

    init {
        assertOnMainThread()
    }

    private val executor = executorFactory()
    private val _state = AtomicReference(initialState)
    override val state: State get() = _state.value
    private val stateSubject = Subject<State>()
    private val labelSubject = Subject<Label>()
    override val isDisposed: Boolean get() = !stateSubject.isActive

    init {
        executor.init(stateSupplier = _state::value, resultConsumer = ::onResult, labelConsumer = ::onLabel)
        bootstrapper?.bootstrap(actionConsumer = ::onAction)
    }

    private fun onAction(action: Action) {
        assertOnMainThread()

        doIfNotDisposed {
            executor.handleAction(action)
        }
    }

    private fun onResult(result: Result) {
        assertOnMainThread()

        doIfNotDisposed {
            changeState {
                reducer.run { it.reduce(result) }
            }
        }
    }

    private inline fun changeState(func: (State) -> State) {
        stateSubject.onNext(_state.updateAndGet(func))
    }

    private fun onLabel(label: Label) {
        assertOnMainThread()

        labelSubject.onNext(label)
    }

    override fun states(observer: Observer<State>): Disposable {
        assertOnMainThread()

        return stateSubject.subscribe(observer, _state.value)
    }

    override fun labels(observer: Observer<Label>): Disposable {
        assertOnMainThread()

        return labelSubject.subscribe(observer)
    }

    override fun accept(intent: Intent) {
        assertOnMainThread()

        doIfNotDisposed {
            executor.handleIntent(intent)
        }
    }

    override fun dispose() {
        assertOnMainThread()

        doIfNotDisposed {
            bootstrapper?.dispose()
            executor.dispose()
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
