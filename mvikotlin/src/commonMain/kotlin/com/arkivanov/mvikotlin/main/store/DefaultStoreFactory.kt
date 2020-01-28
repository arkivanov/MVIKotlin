package com.arkivanov.mvikotlin.main.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

/**
 * An implementation of [StoreFactory] that creates default implementations of [Store]
 */
object DefaultStoreFactory : StoreFactory {

    override fun <Intent : Any, Action : Any, Result : Any, State : Any, Label : Any> create(
        name: String,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, Result, State, Label>,
        reducer: Reducer<State, Result>
    ): Store<Intent, State, Label> =
        DefaultStore(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        )
}
