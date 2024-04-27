package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.annotations.MainThread
import kotlinx.coroutines.CoroutineScope

/**
 * Allows DSL [Bootstrapper][com.arkivanov.mvikotlin.core.store.Bootstrapper]s to launch asynchronous tasks
 * and [dispatch] ``[Action]s.
 *
 * Implements [CoroutineScope] that is cancelled when the [Bootstrapper][com.arkivanov.mvikotlin.core.store.Bootstrapper] is disposed.
 *
 * @see coroutineBootstrapper
 */
interface CoroutineBootstrapperScope<in Action : Any> : CoroutineScope {

    /**
     * Dispatches the [Action] to the [Store][com.arkivanov.mvikotlin.core.store.Store].
     * Must be called on the main thread.
     *
     * @param action an [Action] to be dispatched.
     */
    @MainThread
    fun dispatch(action: Action)
}
