package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlinx.coroutines.CoroutineScope

/**
 * Allows `Intent` and `Action` DSL handlers to launch asynchronous tasks,
 * read the current [State], [dispatch] ``[Message]s, and [publish] ``[Label]s.
 *
 * Implements [CoroutineScope] that is cancelled when the [Executor][com.arkivanov.mvikotlin.core.store.Executor] is disposed.
 *
 * @see coroutineExecutorFactory
 */
@ExperimentalMviKotlinApi
@CoroutineExecutorDslMaker
interface CoroutineExecutorScope<out State : Any, in Message : Any, in Label : Any> : CoroutineScope {

    /**
     * Returns the current [State] of the [Store][com.arkivanov.mvikotlin.core.store.Store].
     */
    val state: State

    /**
     * Dispatches the provided [Message] to the [Reducer][com.arkivanov.mvikotlin.core.store.Reducer].
     * The updated [State] is available immediately after this method returns.
     * Must be called on the main thread.
     *
     * @param message a [Message] to be dispatched to the [Reducer][com.arkivanov.mvikotlin.core.store.Reducer].
     */
    @MainThread
    fun dispatch(message: Message)

    /**
     * Sends the provided [Label] to the [Store][com.arkivanov.mvikotlin.core.store.Store] for publication.
     * Must be called on the main thread.
     *
     * @param label a [Label] to be published.
     */
    @MainThread
    fun publish(label: Label)
}
