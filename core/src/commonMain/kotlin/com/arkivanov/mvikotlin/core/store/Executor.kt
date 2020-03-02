package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

/**
 * `Executor` is the place for business logic.
 * It accepts `Intents` and `Actions` and produces `Results` and `Labels`.
 * **Important**: please pay attention that it must not be a singleton.
 *
 * @see Store
 * @see Reducer
 * @see Bootstrapper
 */
interface Executor<in Intent, in Action, in State, out Result, out Label> {

    /**
     * Initializes the [Executor], called internally by the [Store]
     *
     * @param callbacks an instance of [Callbacks] created by the [Store]
     */
    @MainThread
    fun init(callbacks: Callbacks<State, Result, Label>)

    /**
     * Called by the [Store] for every received `Intent`
     *
     * @param intent an `Intent` received by the [Store]
     */
    @MainThread
    fun handleIntent(intent: Intent) {
    }

    /**
     * Called by the [Store] for every `Action` produced by the [Bootstrapper]
     */
    @MainThread
    fun handleAction(action: Action) {
    }

    /**
     * Disposes the [Executor], called by the [Store] when disposed
     */
    @MainThread
    fun dispose() {
    }

    /**
     * A set of callbacks used for communication between the [Bootstrapper] and the [Store]
     */
    interface Callbacks<out State, in Result, in Label> {
        /**
         * Returns current `State` of the [Store]
         */
        val state: State

        /**
         * Dispatches the `Result` to the [Store], it then goes to the [Reducer].
         * A new `State` will be immediately available after this method returns.
         *
         * @param result a `Result` to be dispatched
         */
        @MainThread
        fun onResult(result: Result)

        /**
         * Publishes the `Label`, it then will be emitted by the [Store]
         *
         * @param label a `Label` to be published
         */
        @MainThread
        fun onLabel(label: Label)
    }
}
