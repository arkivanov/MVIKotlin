package com.arkivanov.mvikotlin.core.main.store

import com.arkivanov.mvikotlin.core.internal.rx.Subject
import com.arkivanov.mvikotlin.core.internal.rx.onComplete
import com.arkivanov.mvikotlin.core.internal.rx.onNext
import com.arkivanov.mvikotlin.core.internal.rx.subscribe
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.updateAndGet

internal class DefaultStore<in Intent, in Action, out Result, out State, out Label>(
    initialState: State,
    bootstrapper: Bootstrapper<Action>? = null,
    executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
    reducer: Reducer<State, Result>
) : Store<Intent, State, Label> {

    private val executor = executorFactory()
    private val _isDisposed = AtomicBoolean()
    override val isDisposed: Boolean get() = _isDisposed.value
    private val _state = AtomicReference(initialState)
    override val state: State get() = _state.value
    private val stateSubject = Subject<State>()
    private val labelSubject = Subject<Label>()

    init {
        executor.init(
            stateSupplier = _state::value,
            resultConsumer = { result ->
                _state
                    .updateAndGet { oldState ->
                        reducer.run { oldState.reduce(result) }
                    }
                    .also { stateSubject.onNext(it) }
            },
            labelConsumer = { labelSubject.onNext(it) }
        )

        bootstrapper?.bootstrap(actionConsumer = executor::handleAction)
    }

    override fun states(observer: Observer<State>): Disposable = stateSubject.subscribe(observer, _state.value)

    override fun labels(observer: Observer<Label>): Disposable = labelSubject.subscribe(observer)

    override fun accept(intent: Intent) {
        executor.handleIntent(intent)
    }

    override fun dispose() {
        _isDisposed.value = true
        executor.dispose()
        stateSubject.onComplete()
        labelSubject.onComplete()
    }
}
