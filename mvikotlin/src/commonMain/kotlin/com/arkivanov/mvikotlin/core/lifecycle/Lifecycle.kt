package com.arkivanov.mvikotlin.core.lifecycle

import com.arkivanov.mvikotlin.core.annotations.MainThread
import kotlin.js.JsName

/**
 * Represents an object that has a lifecycle. Provides a way to subscribe for lifecycle events.
 * Interoperability with Androidx Lifecycle is provided by androidx-lifecycle-interop module.
 *
 * ```
 * INITIALIZED -> onCreate()            -> onStart()            -> onResume()
 *                          \          /            \          /             \
 *                           -> CREATED <-           -> STARTED <-            -> RESUMED
 *                             /          \            /          \             /
 *  DESTROYED <- onDestroy() <-            onStop()  <-            onPause()  <-
 * ```
 */
interface Lifecycle {

    /**
     * Returns current state of the lifecycle.
     * See [State] for more information.
     */
    @MainThread
    val state: State

    /**
     * Subscribes for the lifecycle events with the provided [Callbacks].
     * When subscribed drives the [Callbacks] to the current [State] by calling all callbacks in order.
     *
     * @param callbacks a [Callbacks] that will receive lifecycle events
     */
    @JsName("subscribe")
    @MainThread
    fun subscribe(callbacks: Callbacks)

    /**
     * Unsubscribes the provided [Callbacks] from the lifecycle events.
     *
     * @param callbacks a [Callbacks] to be unsubscribed
     */
    @JsName("unsubscribe")
    @MainThread
    fun unsubscribe(callbacks: Callbacks)

    /**
     * Describes the current state of a [Lifecycle]
     */
    enum class State {
        INITIALIZED, CREATED, STARTED, RESUMED, DESTROYED;

        companion object {
            val VALUES: List<State> = values().toList()
        }
    }

    /**
     * Represents a set of [Lifecycle] callbacks
     */
    interface Callbacks {
        @MainThread
        fun onCreate()

        @MainThread
        fun onStart()

        @MainThread
        fun onResume()

        @MainThread
        fun onPause()

        @MainThread
        fun onStop()

        @MainThread
        fun onDestroy()
    }
}
