package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlin.js.JsName

/**
 * [Executor] is the place for business logic.
 * It accepts [Intent]s and [Action]s and produces [Message]s and [Label]s.
 *
 * **Important**: please pay attention that it must not be a singleton.
 *
 * @see Store
 * @see Reducer
 * @see Bootstrapper
 */
interface Executor<in Intent : Any, Action : Any, in State : Any, out Message : Any, out Label : Any> {

    /**
     * Initializes the [Executor], called internally by the [Store]
     *
     * @param callbacks an instance of [Callbacks] created by the [Store]
     */
    @JsName("init")
    @MainThread
    fun init(callbacks: Callbacks<State, Message, Action, Label>)

    /**
     * Called by the [Store] for every received [Intent].
     *
     * Called on the main thread.
     *
     * @param intent an [Intent] received by the [Store].
     */
    @JsName("executeIntent")
    @MainThread
    fun executeIntent(intent: Intent)

    /**
     * Called by the [Store] for every [Action] produced by the [Bootstrapper].
     *
     * Called on the main thread.
     *
     * @param action an [Action] received by the [Store] from the [Bootstrapper] or from the [Executor] itself.
     */
    @JsName("executeAction")
    @MainThread
    fun executeAction(action: Action)

    /**
     * Disposes the [Executor], called by the [Store] when disposed.
     *
     * Called on the main thread.
     */
    @MainThread
    fun dispose()

    /**
     * A set of callbacks used for communication between the [Bootstrapper] and the [Store]
     */
    interface Callbacks<out State, in Message, in Action, in Label> {
        /**
         * Returns current `State` of the [Store]
         */
        val state: State

        /**
         * Dispatches the specified [message] to the [Store], it then goes to the [Reducer].
         * A new [State] will be immediately available after this method returns.
         *
         * Must be called on the main thread.
         *
         * @param message a [Message] to be dispatched to the [Reducer].
         */
        @JsName("onMessage")
        @MainThread
        fun onMessage(message: Message)

        /**
         * Sends the specified [action] to the [Store] that forwards the [action] back to the [Executor].
         * This is the recommended way of executing actions from the [Executor], as it allows
         * any wrapping Stores to also handle those actions (e.g. logging or time-traveling).
         *
         * Must be called on the main thread.
         *
         * @param action an [Action] to be forwarded back to the [Executor] via [Store].
         */
        @ExperimentalMviKotlinApi
        @JsName("onAction")
        @MainThread
        fun onAction(action: Action)

        /**
         * Publishes the specified [Label], it then will be emitted by the [Store].
         *
         * Must be called on the main thread.
         *
         * @param label a [Label] to be published.
         */
        @JsName("onLabel")
        @MainThread
        fun onLabel(label: Label)
    }
}
