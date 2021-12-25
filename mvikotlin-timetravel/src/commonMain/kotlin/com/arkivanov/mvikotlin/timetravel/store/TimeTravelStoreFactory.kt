package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelControllerHolder

/**
 * An implementation of [StoreFactory] that creates [Store]s with time travel functionality
 */
class TimeTravelStoreFactory : StoreFactory {

    override fun <Intent : Any, Action : Any, Message : Any, State : Any, Label : Any> create(
        name: String?,
        isAutoInit: Boolean,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
        reducer: Reducer<State, Message>
    ): Store<Intent, State, Label> =
        TimeTravelStoreImpl(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        ).also { store ->
            TimeTravelControllerHolder.impl.attachStore(store = store, name = name)
            if (isAutoInit) {
                store.init()
            }
        }

}
