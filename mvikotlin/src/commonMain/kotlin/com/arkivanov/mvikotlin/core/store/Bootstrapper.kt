package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import kotlin.js.JsName

/**
 * Used for [Store] bootstrapping. Dispatch initial `Actions`, subscribe to data sources or do any other initializations.
 * All the dispatched `Actions` will be passed to the [Executor] via its [Executor.executeAction] method.
 *
 * @param Action type of Action
 *
 * @see Store
 * @see Executor
 */
interface Bootstrapper<out Action : Any> {

    /**
     * Initializes the [Bootstrapper], called internally by the [Store]
     *
     * @param actionConsumer a consumer of Actions to be used by the Bootstrapper, must be invoked on the main thread
     */
    @JsName("init")
    @MainThread
    fun init(actionConsumer: (Action) -> Unit)

    /**
     * Called by the [Store] at some point during initialization
     */
    @MainThread
    operator fun invoke()

    /**
     * Disposes the [Bootstrapper], called by the [Store] when disposed
     */
    @MainThread
    fun dispose()
}
