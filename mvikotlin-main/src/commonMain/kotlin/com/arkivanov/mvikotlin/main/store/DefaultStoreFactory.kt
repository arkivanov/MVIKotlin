package com.arkivanov.mvikotlin.main.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

/**
 * An implementation of the [StoreFactory] that creates default implementations of the [Store].
 *
 * @param isAutoInitByDefault the value is used as default for [StoreFactory.create] (`initialState`) argument
 */
class DefaultStoreFactory(
    override val isAutoInitByDefault: Boolean = true
) : StoreFactory {

    constructor() : this(
        isAutoInitByDefault = true
    )

    override fun <Intent : Any, Action : Any, Result : Any, State : Any, Label : Any> create(
        name: String?,
        isAutoInit: Boolean,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
        reducer: Reducer<State, Result>
    ): Store<Intent, State, Label> =
        DefaultStore(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executor = executorFactory(),
            reducer = reducer
        ).apply {
            if (isAutoInit) {
                init()
            }
        }
}
