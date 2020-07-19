package com.arkivanov.mvikotlin.extensions.androidx.lifecycle

import androidx.lifecycle.LifecycleObserver
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle as AndroidLifecycle

internal class AndroidLifecycleAdapter(
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
        val observer = AndroidLifecycleObserverAdapter(callbacks)
        callbacksToObserver[callbacks] = observer
        androidLifecycle.addObserver(observer)
    }

    override fun unsubscribe(callbacks: Lifecycle.Callbacks) {
        val observer = callbacksToObserver.remove(callbacks) ?: return
        androidLifecycle.removeObserver(observer)
    }
}
