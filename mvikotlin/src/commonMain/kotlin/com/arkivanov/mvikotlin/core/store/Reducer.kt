package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

/**
 * Applies `Results` to `States`. Normally should be a singleton.
 *
 * @see Store
 * @see Executor
 */
interface Reducer<State, in Result> {

    /**
     * Accepts the current `State` and the `Result` and produces the new `State`.
     * Called for every `Result` produced by the `Executor`.
     *
     * @receiver current `State` of the `Store`
     * @param result a `Result` dispatched by the `Executor`
     *
     * @return a new `State`
     */
    @MainThread
    fun State.reduce(result: Result): State
}
