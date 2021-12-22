package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import kotlin.js.JsName

/**
 * `Executor` is the place for business logic.
 * It accepts `Intents` and `Actions` and produces `Results` and `Labels`.
 * **Important**: please pay attention that it must not be a singleton.
 *
 * @see Store
 * @see Reducer
 * @see Bootstrapper
 */
interface Executor<in Intent : Any, in Action : Any, in State : Any, out Result : Any, out Label : Any> {

    /**
     * Initializes the [Executor], called internally by the [Store]
     *
     * @param callbacks an instance of [Callbacks] created by the [Store]
     */
    @JsName("init")
    @MainThread
    fun init(callbacks: Callbacks<State, Result, Label>)

    /**
     * Called by the [Store] for every received `Intent`
     *
     * @param intent an `Intent` received by the [Store]
     * @param getState a function that returns the current `State` of the [Store], must be called on Main thread
     */
    @JsName("handleIntent")
    @MainThread
    fun executeIntent(intent: Intent, @MainThread getState: () -> State)

    /**
     * Called by the [Store] for every `Action` produced by the [Bootstrapper]
     *
     * @param action an `Action` produced by the [Bootstrapper]
     * @param getState a function that returns the current `State` of the [Store], must be called on Main thread
     */
    @JsName("handleAction")
    @MainThread
    fun executeAction(action: Action, @MainThread getState: () -> State)

    /**
     * Disposes the [Executor], called by the [Store] when disposed
     */
    @MainThread
    fun dispose()

    /**
     * A set of callbacks used for communication between the [Bootstrapper] and the [Store]
     */
    interface Callbacks<out State, in Result, in Label> {
        /**
         * Dispatches the `Result` to the [Store], it then goes to the [Reducer].
         * A new `State` will be immediately available after this method returns.
         *
         * @param result a `Result` to be dispatched
         */
        @JsName("onResult")
        @MainThread
        fun onResult(result: Result)

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
