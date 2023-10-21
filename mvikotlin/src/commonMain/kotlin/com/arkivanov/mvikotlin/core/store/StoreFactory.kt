package com.arkivanov.mvikotlin.core.store

import kotlin.js.JsName

/**
 * Creates instances of [Store]s using the provided components.
 * You can create different [Store] wrappers and combine them depending on circumstances.
 */
interface StoreFactory {

    /**
     * Creates an implementation of [Store].
     * Must be called only on the main thread if [autoInit] argument is `true` (default).
     * Can be called on any thread if the [autoInit] is `false`.
     *
     * @param name a name of the [Store] being created, used for logging, time traveling, etc.
     * @param autoInit if `true` then the [Store] will be automatically initialized after creation,
     * otherwise you should call [Store.init] manually, default value is `true`
     * @param initialState an initial state of the [Store].
     * @param bootstrapper an optional [Bootstrapper] for the [Store], automatically called after
     * the initialization.
     * @param executorFactory a factory that creates an [Executor] for the [Store]. The returned
     * [Executor] *must not* be a singleton, a *new* instance must be created every time.
     * @param reducer a [Reducer] for the [Store].
     */
    @JsName("create")
    fun <Intent : Any, Action : Any, Message : Any, State : Any, Label : Any> create(
        name: String? = null,
        autoInit: Boolean = true,
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
