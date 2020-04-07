package com.arkivanov.mvikotlin.androidxlifecycleinterop

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle as AndroidLifecycle

/**
 * Converts Androidx [Lifecycle][AndroidLifecycle] to MviKotlin [Lifecycle]
 */
fun AndroidLifecycle.asMviLifecycle(): Lifecycle = AndroidxLifecycleInterop(this)

private class AndroidxLifecycleInterop(
    private val androidLifecycle: AndroidLifecycle
) : Lifecycle {

    private val callbacksToObserver = HashMap<Lifecycle.Callbacks, LifecycleObserver>()

    override val state: Lifecycle.State
        get() =
            when (androidLifecycle.currentState) {
                AndroidLifecycle.State.DESTROYED -> Lifecycle.State.DESTROYED
                AndroidLifecycle.State.INITIALIZED -> Lifecycle.State.INITIALIZED
                AndroidLifecycle.State.CREATED -> Lifecycle.State.CREATED
                AndroidLifecycle.State.STARTED -> Lifecycle.State.STARTED
                AndroidLifecycle.State.RESUMED -> Lifecycle.State.RESUMED
            }

    override fun subscribe(callbacks: Lifecycle.Callbacks) {
        val observer = callbacks.toLifecycleObserver()
        callbacksToObserver[callbacks] = observer
        androidLifecycle.addObserver(observer)
    }

    override fun unsubscribe(callbacks: Lifecycle.Callbacks) {
        val observer = callbacksToObserver.remove(callbacks) ?: return
        androidLifecycle.removeObserver(observer)
    }
}

private fun Lifecycle.Callbacks.toLifecycleObserver(): LifecycleObserver =
    object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            this@toLifecycleObserver.onCreate()
        }

        override fun onStart(owner: LifecycleOwner) {
            this@toLifecycleObserver.onStart()
        }

        override fun onResume(owner: LifecycleOwner) {
            this@toLifecycleObserver.onResume()
        }

        override fun onPause(owner: LifecycleOwner) {
            this@toLifecycleObserver.onPause()
        }

        override fun onStop(owner: LifecycleOwner) {
            this@toLifecycleObserver.onStop()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            this@toLifecycleObserver.onDestroy()
        }
    }
