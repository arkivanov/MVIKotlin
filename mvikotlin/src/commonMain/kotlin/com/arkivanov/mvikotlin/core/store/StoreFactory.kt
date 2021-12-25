package com.arkivanov.mvikotlin.core.store

import kotlin.js.JsName

/**
 * Creates instances of [Store]s using the provided components.
 * You can create different [Store] wrappers and combine them depending on circumstances.
 */
interface StoreFactory {

    /**
     * Creates an implementation of [Store].
     * Must be called only on the main thread if [isAutoInit] argument is true (default).
     * Can be called on any thread if the [isAutoInit] is false.
     *
     * @param name a name of the [Store] being created, used for logging, time traveling, etc.
     * @param isAutoInit if `true` then the [Store] will be automatically initialized after creation,
     * otherwise call [Store.init] manually, default value is `true`
     */
    @JsName("create")
    fun <Intent : Any, Action : Any, Message : Any, State : Any, Label : Any> create(
        name: String? = null,
        isAutoInit: Boolean = true,
        initialState: State,
        bootstrapper: Bootstrapper<Action>? = null,
        executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
        @Suppress("UNCHECKED_CAST")
        reducer: Reducer<State, Message> = bypassReducer as Reducer<State, Any>
    ): Store<Intent, State, Label>

    private companion object {
        private val bypassReducer: Reducer<Any, Any> = Reducer { this }
    }
}
