package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.timetravel.controller.attachTimeTravelStore

/**
 * An implementation of [StoreFactory] that creates [Store]s with time travel functionality
 *
 * @param fallback a [StoreFactory] to be used when no name is supplied
 */
class TimeTravelStoreFactory(
    private val fallback: StoreFactory
) : StoreFactory {

    override fun <Intent : Any, Action : Any, Result : Any, State : Any, Label : Any> create(
        name: String?,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
        reducer: Reducer<State, Result>
    ): Store<Intent, State, Label> =
        if (name == null) {
            fallback.create(
                initialState = initialState,
                bootstrapper = bootstrapper,
                executorFactory = executorFactory,
                reducer = reducer
            )
        } else {
            TimeTravelStoreImpl(
                name = name,
                initialState = initialState,
                bootstrapper = bootstrapper,
                executorFactory = executorFactory,
                reducer = reducer
            )
                .also(::attachTimeTravelStore)
        }
}
