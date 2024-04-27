package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlinx.coroutines.CoroutineScope

/**
 * Allows `Intent` and `Action` DSL handlers to launch asynchronous tasks,
 * read the current [State], [dispatch] `Messages`, [forward] `Actions`, and [publish] `Labels`.
 *
 * Implements [CoroutineScope] that is cancelled when the [Executor][com.arkivanov.mvikotlin.core.store.Executor] is disposed.
 *
 * @see coroutineExecutorFactory
 */
@ExperimentalMviKotlinApi
@CoroutineExecutorDslMaker
interface CoroutineExecutorScope<out State : Any, in Message : Any, in Action : Any, in Label : Any> : CoroutineScope {

    /**
     * Returns the current [State] of the [Store][com.arkivanov.mvikotlin.core.store.Store].
     */
    fun state(): State

    /**
     * Dispatches the provided [Message] to the [Reducer][com.arkivanov.mvikotlin.core.store.Reducer].
     * The updated [State] is available immediately after this method returns.
     *
     * Must be called on the main thread.
     *
     * @param message a [Message] to be dispatched to the [Reducer][com.arkivanov.mvikotlin.core.store.Reducer].
     */
    @MainThread
    fun dispatch(message: Message)

    /**
     * Sends the provided [action] to the [Store][com.arkivanov.mvikotlin.core.store.Store]
     * and then forwards the [action] back to the [Executor][com.arkivanov.mvikotlin.core.store.Executor].
     * This is the recommended way of executing actions from the [Executor][com.arkivanov.mvikotlin.core.store.Executor],
     * as it allows any wrapping Stores to also handle those actions (e.g. logging or time-traveling).
     *
     * Must be called on the main thread.
     *
     * @param action an [Action] to be forwarded back to the [Executor][com.arkivanov.mvikotlin.core.store.Executor]
     * via [Store][com.arkivanov.mvikotlin.core.store.Store].
     */
    @MainThread
    fun forward(action: Action)

    /**
     * Sends the provided [Label] to the [Store][com.arkivanov.mvikotlin.core.store.Store] for publication.
     *
     * Must be called on the main thread.
     *
     * @param label a [Label] to be published.
     */
    @MainThread
    fun publish(label: Label)
}
