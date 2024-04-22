package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Executor.Callbacks
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.internal.atomic
import com.arkivanov.mvikotlin.core.utils.internal.initialize
import com.arkivanov.mvikotlin.core.utils.internal.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * An abstract implementation of the [Executor] that exposes a [CoroutineScope] for coroutines launching.
 *
 * @param mainContext a [CoroutineContext] to be used by the exposed [CoroutineScope]
 */
open class CoroutineExecutor<in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main
) : Executor<Intent, Action, State, Message, Label> {

    private val callbacks = atomic<Callbacks<State, Message, Action, Label>>()

    /**
     * A [CoroutineScope] that can be used by the [CoroutineExecutor] descendants to launch coroutines.
     * The [CoroutineScope] is automatically cancelled on dispose.
     */
    protected val scope: CoroutineScope = CoroutineScope(mainContext)

    final override fun init(callbacks: Callbacks<State, Message, Action, Label>) {
        this.callbacks.initialize(callbacks)
    }

    /**
     * Returns the *current* [State] of the [Store][com.arkivanov.mvikotlin.core.store.Store].
     *
     * The [State] may change between subsequent invocations if accessed on a non-main thread.
     */
    protected fun state(): State =
        callbacks.requireValue().state

    override fun executeIntent(intent: Intent) {
        // no-op
    }

    override fun executeAction(action: Action) {
        // no-op
    }

    override fun dispose() {
        scope.cancel()
    }

    /**
     * Sends the provided [action] to the [Store] and then forwards the [action] back to the [Executor].
     * This is the recommended way of executing actions from the [Executor], as it allows
     * any wrapping Stores to also handle those actions (e.g. logging or time-traveling).
     *
     * Must be called on the main thread.
     *
     * @param action an [Action] to be forwarded back to the [Executor] via [Store].
     */
    protected fun forward(action: Action) {
        callbacks.requireValue().onAction(action)
    }

    /**
     * Dispatches the provided [Message] to the [Reducer].
     * The updated [State] will be available immediately after this method returns.
     *
     * Must be called on the main thread.
     *
     * @param message a [Message] to be dispatched to the [Reducer].
     */
    @MainThread
    protected fun dispatch(message: Message) {
        callbacks.requireValue().onMessage(message)
    }

    /**
     * Sends the provided [Label] to the [Store] for publication.
     *
     * Must be called on the main thread.
     *
     * @param label a [Label] to be published.
     */
    @MainThread
    protected fun publish(label: Label) {
        callbacks.requireValue().onLabel(label)
    }
}
