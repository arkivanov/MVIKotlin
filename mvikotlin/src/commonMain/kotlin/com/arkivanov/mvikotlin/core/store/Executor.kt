package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import kotlin.js.JsName

/**
 * `Executor` is the place for business logic.
 * It accepts `Intents` and `Actions` and produces `Messages` and `Labels`.
 * **Important**: please pay attention that it must not be a singleton.
 *
 * @see Store
 * @see Reducer
 * @see Bootstrapper
 */
interface Executor<in Intent : Any, in Action : Any, in State : Any, out Message : Any, out Label : Any> {

    /**
     * Initializes the [Executor], called internally by the [Store]
     *
     * @param callbacks an instance of [Callbacks] created by the [Store]
     */
    @JsName("init")
    @MainThread
    fun init(callbacks: Callbacks<State, Message, Label>)

    /**
     * Called by the [Store] for every received `Intent`
     *
     * @param intent an `Intent` received by the [Store]
     */
    @JsName("executeIntent")
    @MainThread
    fun executeIntent(intent: Intent)

    /**
     * Called by the [Store] for every `Action` produced by the [Bootstrapper]
     */
    @JsName("executeAction")
    @MainThread
    fun executeAction(action: Action)

    /**
     * Disposes the [Executor], called by the [Store] when disposed
     */
    @MainThread
    fun dispose()

    /**
     * A set of callbacks used for communication between the [Bootstrapper] and the [Store]
     */
    interface Callbacks<out State, in Message, in Label> {
        /**
         * Returns current `State` of the [Store]
         */
        val state: State

        /**
         * Dispatches the `Message` to the [Store], it then goes to the [Reducer].
         * A new `State` will be immediately available after this method returns.
         *
         * @param message a `Message` to be dispatched to the [Reducer]
         */
        @JsName("onMessage")
        @MainThread
        fun onMessage(message: Message)

        /**
         * Publishes the `Label`, it then will be emitted by the [Store]
         *
         * @param label a `Label` to be published
         */
        @JsName("onLabel")
        @MainThread
        fun onLabel(label: Label)
    }
}
