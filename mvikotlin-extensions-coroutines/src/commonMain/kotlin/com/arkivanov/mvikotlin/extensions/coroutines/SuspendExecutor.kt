package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Executor.Callbacks
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * An abstract implementation of the [Executor] that provides interoperability with coroutines.
 * All coroutines are launched in a scope which closes when the [Executor] is disposed.
 */
@Deprecated(
    message = "Please use CoroutineExecutor",
    replaceWith = ReplaceWith("CoroutineExecutor<Intent, Action, State, Message, Label>")
)
open class SuspendExecutor<in Intent : Any, in Action : Any, in State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main
) : Executor<Intent, Action, State, Message, Label> {

    private val callbacks = atomic<Callbacks<State, Message, Label>>()
    private val getState: () -> State = { callbacks.requireValue().state }
    private val scope = CoroutineScope(mainContext)

    final override fun init(callbacks: Callbacks<State, Message, Label>) {
        this.callbacks.initialize(callbacks)
    }

    final override fun executeIntent(intent: Intent) {
        scope.launch {
            executeIntent(intent, getState)
        }
    }

    /**
     * A suspending variant of the [Executor.executeIntent] method.
     * The coroutine is launched in a scope which closes when the [Executor] is disposed.
     *
     * @param intent an `Intent` received by the [Store]
     * @param getState a `State` supplier that returns the *current* `State` of the [Store]
     */
    @MainThread
    protected open suspend fun executeIntent(intent: Intent, getState: () -> State) {
    }

    final override fun executeAction(action: Action) {
        scope.launch {
            executeAction(action, getState)
        }
    }

    /**
     * Called for every `Action` produced by the [Executor]
     * The coroutine is launched in a scope which closes when the [Executor] is disposed.
     *
     * @param action an `Action` produced by the [Bootstrapper]
     * @param getState a `State` supplier that returns the *current* `State` of the [Store]
     */
    @MainThread
    protected open suspend fun executeAction(action: Action, getState: () -> State) {
    }

    override fun dispose() {
        scope.cancel()
    }

    /**
     * Dispatches the provided `Message` to the [Reducer].
     * The updated `State` will be available immediately after this method returns.
     *
     * @param message a `Message` to be dispatched to the `Reducer`
     */
    @MainThread
    protected fun dispatch(message: Message) {
        callbacks.requireValue().onMessage(message)
    }

    /**
     * Sends the provided `Label` to the [Store] for publication
     *
     * @param label a `Label` to be published
     */
    @MainThread
    protected fun publish(label: Label) {
        callbacks.requireValue().onLabel(label)
    }
}
