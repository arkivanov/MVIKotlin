package com.arkivanov.mvikotlin.core.lifecycle

/**
 * Subscribes to the [Lifecycle], listens for [Lifecycle.Callbacks.onDestroy] and
 * calls the provided callback.
 *
 * @receiver the [Lifecycle] to be subscribed
 * @param onDestroy a callback that will be called when [Lifecycle.Callbacks.onDestroy] is called
 */
inline fun Lifecycle.doOnDestroy(crossinline onDestroy: () -> Unit) {
    subscribe(
        object : Lifecycle.Callbacks {
            override fun onDestroy() {
                onDestroy.invoke()
            }
        }
    )
}

/**
 * Subscribes to the [Lifecycle], listens for [Lifecycle.Callbacks.onCreate] and [Lifecycle.Callbacks.onDestroy] and
 * calls the provided callbacks respectively.
 *
 * @receiver the [Lifecycle] to be subscribed
 * @param onCreate a callback that will be called when [Lifecycle.Callbacks.onCreate] is called
 * @param onDestroy a callback that will be called when [Lifecycle.Callbacks.onDestroy] is called
 */
inline fun Lifecycle.doOnCreateDestroy(crossinline onCreate: () -> Unit, crossinline onDestroy: () -> Unit) {
    subscribe(
        object : Lifecycle.Callbacks {
            override fun onCreate() {
                onCreate.invoke()
            }

            override fun onDestroy() {
                onDestroy.invoke()
            }
        }
    )
}

/**
 * Subscribes to the [Lifecycle], listens for [Lifecycle.Callbacks.onStop] and
 * calls the provided callback.
 *
 * @receiver the [Lifecycle] to be subscribed
 * @param onStop a callback that will be called when [Lifecycle.Callbacks.onStop] is called
 */
inline fun Lifecycle.doOnStop(crossinline onStop: () -> Unit) {
    subscribe(
        object : Lifecycle.Callbacks {
            override fun onStop() {
                onStop.invoke()
            }
        }
    )
}

/**
 * Subscribes to the [Lifecycle], listens for [Lifecycle.Callbacks.onStart] and [Lifecycle.Callbacks.onStop] and
 * calls the provided callbacks respectively.
 *
 * @receiver the [Lifecycle] to be subscribed
 * @param onStart a callback that will be called when [Lifecycle.Callbacks.onStart] is called
 * @param onStop a callback that will be called when [Lifecycle.Callbacks.onStop] is called
 */
inline fun Lifecycle.doOnStartStop(crossinline onStart: () -> Unit, crossinline onStop: () -> Unit) {
    subscribe(
        object : Lifecycle.Callbacks {
            override fun onStart() {
                onStart.invoke()
            }

            override fun onStop() {
                onStop.invoke()
            }
        }
    )
}

/**
 * Subscribes to the [Lifecycle], listens for [Lifecycle.Callbacks.onPause] and
 * calls the provided callback.
 *
 * @receiver the [Lifecycle] to be subscribed
 * @param onPause a callback that will be called when [Lifecycle.Callbacks.onPause] is called
 */
inline fun Lifecycle.doOnPause(crossinline onPause: () -> Unit) {
    subscribe(
        object : Lifecycle.Callbacks {
            override fun onPause() {
                onPause.invoke()
            }
        }
    )
}

/**
 * Subscribes to the [Lifecycle], listens for [Lifecycle.Callbacks.onResume] and [Lifecycle.Callbacks.onPause] and
 * calls the provided callbacks respectively.
 *
 * @receiver the [Lifecycle] to be subscribed
 * @param onResume a callback that will be called when [Lifecycle.Callbacks.onResume] is called
 * @param onPause a callback that will be called when [Lifecycle.Callbacks.onPause] is called
 */
inline fun Lifecycle.doOnResumePause(crossinline onResume: () -> Unit, crossinline onPause: () -> Unit) {
    subscribe(
        object : Lifecycle.Callbacks {
            override fun onResume() {
                onResume.invoke()
            }

            override fun onPause() {
                onPause.invoke()
            }
        }
    )
}
