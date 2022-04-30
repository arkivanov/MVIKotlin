package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlinx.coroutines.CoroutineScope

/**
 * Allows DSL [Bootstrapper][com.arkivanov.mvikotlin.core.store.Bootstrapper]s to launch asynchronous tasks
 * and [dispatch] ``[Action]s.
 *
 * Implements [CoroutineScope] that is cancelled when the [Bootstrapper][com.arkivanov.mvikotlin.core.store.Bootstrapper] is disposed.
 *
 * @see coroutineBootstrapper
 */
@ExperimentalMviKotlinApi
interface CoroutineBootstrapperScope<in Action : Any> : CoroutineScope {

    /**
     * Dispatches the [Action] to the [Store][com.arkivanov.mvikotlin.core.store.Store].
     *
     * @param action an [Action] to be dispatched.
     */
    fun dispatch(action: Action)
}
