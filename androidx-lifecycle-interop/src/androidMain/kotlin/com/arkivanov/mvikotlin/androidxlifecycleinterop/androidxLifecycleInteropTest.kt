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

internal class AndroidxLifecycleInterop(
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
        val observer = AndroidLifecycleObserverDelegate(callbacks)
        callbacksToObserver[callbacks] = observer
        androidLifecycle.addObserver(observer)
    }

    override fun unsubscribe(callbacks: Lifecycle.Callbacks) {
        val observer = callbacksToObserver.remove(callbacks) ?: return
        androidLifecycle.removeObserver(observer)
    }
}

private class AndroidLifecycleObserverDelegate(
    private val delegate: Lifecycle.Callbacks
) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        delegate.onCreate()
    }

    override fun onStart(owner: LifecycleOwner) {
        delegate.onStart()
    }

    override fun onResume(owner: LifecycleOwner) {
        delegate.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        delegate.onPause()
    }

    override fun onStop(owner: LifecycleOwner) {
        delegate.onStop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        delegate.onDestroy()
    }
}
