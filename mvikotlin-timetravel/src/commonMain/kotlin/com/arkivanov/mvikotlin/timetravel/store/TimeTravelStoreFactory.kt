package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.StoreSerializers
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelControllerHolder

/**
 * An implementation of [StoreFactory] that creates [Store]s with time travel functionality
 */
class TimeTravelStoreFactory : StoreFactory {

    override fun <Intent : Any, Action : Any, Message : Any, State : Any, Label : Any> create(
        name: String?,
        autoInit: Boolean,
        initialState: State,
        serializers: StoreSerializers<Intent, Action, Message, State, Label>?,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
        reducer: Reducer<State, Message>
    ): Store<Intent, State, Label> =
        TimeTravelStoreImpl(
            initialState = initialState,
            serializers = serializers,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer,
            onInit = { TimeTravelControllerHolder.impl.attachStore(store = it, name = name) },
        ).also { store ->
            if (autoInit) {
                store.init()
            }
        }

}
