package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import kotlin.js.JsName

/**
 * A [Reducer] is a function that takes the current `State` and a `Message`
 * as arguments, and returns a new `State` as a result.
 *
 * @see Store
 * @see Executor
 */
fun interface Reducer<State, in Message> {

    /**
     * Accepts the current `State` and a `Message` and produces a new `State`.
     * Called for every `Message` produced by the [Executor].
     *
     * @receiver the current `State` of the [Store]
     * @param msg a `Message` dispatched by the [Executor]
     *
     * @return a new `State`
     */
    @JsName("reduce")
    @MainThread
    fun State.reduce(msg: Message): State
}
