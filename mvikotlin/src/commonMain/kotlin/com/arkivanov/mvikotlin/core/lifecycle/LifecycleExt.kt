package com.arkivanov.mvikotlin.core.lifecycle

inline fun Lifecycle.doOnDestroy(crossinline onDestroy: () -> Unit) {
    register(
        object : Lifecycle.Callbacks {
            override fun onDestroy() {
                onDestroy.invoke()
            }
        }
    )
}

inline fun Lifecycle.doOnCreateDestroy(crossinline onCreate: () -> Unit, crossinline onDestroy: () -> Unit) {
    register(
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

inline fun Lifecycle.doOnStop(crossinline onStop: () -> Unit) {
    register(
        object : Lifecycle.Callbacks {
            override fun onStop() {
                onStop.invoke()
            }
        }
    )
}

inline fun Lifecycle.doOnStartStop(crossinline onStart: () -> Unit, crossinline onStop: () -> Unit) {
    register(
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

inline fun Lifecycle.doOnPause(crossinline onPause: () -> Unit) {
    register(
        object : Lifecycle.Callbacks {
            override fun onPause() {
                onPause.invoke()
            }
        }
    )
}

inline fun Lifecycle.doOnResumePause(crossinline onResume: () -> Unit, crossinline onPause: () -> Unit) {
    register(
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
