package com.arkivanov.mvikotlin.core.lifecycle

import com.badoo.reaktive.utils.ensureNeverFrozen

/**
 * Implements both [Lifecycle] and [Lifecycle.Callbacks]
 */
class LifecycleRegistry : Lifecycle, Lifecycle.Callbacks {

    init {
        ensureNeverFrozen()
    }

    private var set = emptySet<Lifecycle.Callbacks>()
    private var _state = Lifecycle.State.INITIALIZED
    override val state: Lifecycle.State get() = _state

    override fun subscribe(callbacks: Lifecycle.Callbacks) {
        if (_state !== Lifecycle.State.DESTROYED) {
            set = set + callbacks
            driveToCurrentState(callbacks)
        }
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

    override fun unsubscribe(callbacks: Lifecycle.Callbacks) {
        set = set - callbacks
    }

    override fun onCreate() {
        setState(required = Lifecycle.State.INITIALIZED, newState = Lifecycle.State.CREATED)
        set.forEach(Lifecycle.Callbacks::onCreate)
    }

    override fun onStart() {
        setState(required = Lifecycle.State.CREATED, newState = Lifecycle.State.STARTED)
        set.forEach(Lifecycle.Callbacks::onStart)
    }

    override fun onResume() {
        setState(required = Lifecycle.State.STARTED, newState = Lifecycle.State.RESUMED)
        set.forEach(Lifecycle.Callbacks::onResume)
    }

    override fun onPause() {
        setState(required = Lifecycle.State.RESUMED, newState = Lifecycle.State.STARTED)
        set.reversed().forEach(Lifecycle.Callbacks::onPause)
    }

    override fun onStop() {
        set.reversed().forEach(Lifecycle.Callbacks::onStop)
        setState(required = Lifecycle.State.STARTED, newState = Lifecycle.State.CREATED)
    }

    override fun onDestroy() {
        set.reversed().forEach(Lifecycle.Callbacks::onDestroy)
        set = emptySet()
        setState(required = Lifecycle.State.CREATED, newState = Lifecycle.State.DESTROYED)
    }

    private fun setState(required: Lifecycle.State, newState: Lifecycle.State) {
        check(_state == required) {
            "Expected lifecycle state $required, actual $_state"
        }

        _state = newState
    }
}
