package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue

fun <Intent : Any, State : Any> StoreFactory.create(
    name: String,
    initialState: State,
    reducer: Reducer<State, Intent>
): Store<Intent, State, Nothing> =
    create(
        name = name,
        initialState = initialState,
        executorFactory = ::BypassExecutor,
        reducer = reducer
    )

private class BypassExecutor<Intent : Any, in State : Any> : Executor<Intent, Nothing, State, Intent, Nothing> {
    private val callbacks = lateinitAtomicReference<Executor.Callbacks<State, Intent, Nothing>>()

    override fun init(callbacks: Executor.Callbacks<State, Intent, Nothing>) {
        this.callbacks.initialize(callbacks)
    }

    override fun handleIntent(intent: Intent) {
        callbacks.requireValue.onResult(intent)
    }
}
