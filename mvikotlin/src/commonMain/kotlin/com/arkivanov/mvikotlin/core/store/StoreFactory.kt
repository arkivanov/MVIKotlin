package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

/**
 * Creates instances of [Store]s using the provided components.
 * You can create different [Store] wrappers and combine them depending on circumstances.
 */
interface StoreFactory {

    @MainThread
    fun <Intent : Any, Action : Any, Result : Any, State : Any, Label : Any> create(
        name: String? = null,
        initialState: State,
        bootstrapper: Bootstrapper<Action>? = null,
        executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
        @Suppress("UNCHECKED_CAST")
        reducer: Reducer<State, Result> = bypassReducer as Reducer<State, Any>
    ): Store<Intent, State, Label>

    private companion object {
        private val bypassReducer: Reducer<Any, Any> =
            object : Reducer<Any, Any> {
                override fun Any.reduce(result: Any): Any = this
            }
    }
}
