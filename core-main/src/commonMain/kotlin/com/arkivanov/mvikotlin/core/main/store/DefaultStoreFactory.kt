package com.arkivanov.mvikotlin.core.main.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

object DefaultStoreFactory : StoreFactory {

    override fun <Intent, Action, Result, State, Label> create(
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
        reducer: Reducer<State, Result>
    ): Store<Intent, State, Label> =
        DefaultStore(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        )
}
