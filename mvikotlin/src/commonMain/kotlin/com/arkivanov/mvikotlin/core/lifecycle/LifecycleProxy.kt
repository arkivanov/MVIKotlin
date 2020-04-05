package com.arkivanov.mvikotlin.core.lifecycle

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

class LifecycleProxy : Lifecycle {

    private val set = AtomicReference(emptySet<Lifecycle.Callbacks>())
    private val _state = AtomicReference(Lifecycle.State.INITIALIZED)
    override val state: Lifecycle.State get() = _state.value

    override fun register(callbacks: Lifecycle.Callbacks) {
        set.update { it + callbacks }
        driveToCurrentState(callbacks)
    }

    private fun driveToCurrentState(callbacks: Lifecycle.Callbacks) {
        for (index in Lifecycle.State.CREATED.ordinal..state.ordinal) {
            when (Lifecycle.State.VALUES[index]) {
                Lifecycle.State.INITIALIZED,
                Lifecycle.State.DESTROYED -> Unit
                Lifecycle.State.CREATED -> callbacks.onCreate()
                Lifecycle.State.STARTED -> callbacks.onStart()
                Lifecycle.State.RESUMED -> callbacks.onResume()
            }.let {}
        }
    }

    override fun unregister(callbacks: Lifecycle.Callbacks) {
        set.update { it - callbacks }
    }

    fun onCreate() {
        setState(required = Lifecycle.State.INITIALIZED, newState = Lifecycle.State.CREATED)
        set.value.forEach(Lifecycle.Callbacks::onCreate)
    }

    fun onStart() {
        setState(required = Lifecycle.State.CREATED, newState = Lifecycle.State.STARTED)
        set.value.forEach(Lifecycle.Callbacks::onStart)
    }

    fun onResume() {
        setState(required = Lifecycle.State.STARTED, newState = Lifecycle.State.RESUMED)
        set.value.forEach(Lifecycle.Callbacks::onResume)
    }

    fun onPause() {
        setState(required = Lifecycle.State.RESUMED, newState = Lifecycle.State.STARTED)
        set.value.forEach(Lifecycle.Callbacks::onPause)
    }

    fun onStop() {
        set.value.forEach(Lifecycle.Callbacks::onStop)
        setState(required = Lifecycle.State.STARTED, newState = Lifecycle.State.CREATED)
    }

    fun onDestroy() {
        set.value.forEach(Lifecycle.Callbacks::onDestroy)
        setState(required = Lifecycle.State.CREATED, newState = Lifecycle.State.DESTROYED)
    }

    private fun setState(required: Lifecycle.State, newState: Lifecycle.State) {
        require(_state.compareAndSet(required, newState)) {
            "Expected lifecycle state $required, actual ${_state.value}"
        }
    }
}
