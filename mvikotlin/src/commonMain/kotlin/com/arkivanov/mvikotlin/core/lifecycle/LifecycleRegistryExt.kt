package com.arkivanov.mvikotlin.core.lifecycle

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle.State

fun LifecycleRegistry.resume() {
    if (state == State.INITIALIZED) {
        onCreate()
    }

    if (state == State.CREATED) {
        onStart()
    }

    if (state == State.STARTED) {
        onResume()
    }
}

fun LifecycleRegistry.stop() {
    if (state == State.INITIALIZED) {
        onCreate()
    } else {
        if (state == State.RESUMED) {
            onPause()
        }

        if (state == State.STARTED) {
            onStop()
        }
    }
}

fun LifecycleRegistry.destroy() {
    stop()

    if (state == State.CREATED) {
        onDestroy()
    }
}
