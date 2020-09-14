package com.arkivanov.mvikotlin.core.lifecycle

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle.Callbacks
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle.State
import com.badoo.reaktive.utils.ensureNeverFrozen

/**
 * Implements both [Lifecycle] and [Lifecycle.Callbacks]
 */
class LifecycleRegistry : Lifecycle, Callbacks {

    init {
        ensureNeverFrozen()
    }

    private var set = emptySet<Callbacks>()
    private var _state = State.INITIALIZED
    override val state: State get() = _state

    override fun subscribe(callbacks: Callbacks) {
        if (_state !== State.DESTROYED) {
            set = set + callbacks
            driveToCurrentState(callbacks)
        }
    }

    private fun driveToCurrentState(callbacks: Callbacks) {
        val state = _state
        if (state >= State.CREATED) {
            callbacks.onCreate()
        }
        if (state >= State.STARTED) {
            callbacks.onStart()
        }
        if (state >= State.RESUMED) {
            callbacks.onResume()
        }
    }

    override fun unsubscribe(callbacks: Callbacks) {
        set = set - callbacks
    }

    override fun onCreate() {
        setState(required = State.INITIALIZED, newState = State.CREATED)
        set.forEach(Callbacks::onCreate)
    }

    override fun onStart() {
        setState(required = State.CREATED, newState = State.STARTED)
        set.forEach(Callbacks::onStart)
    }

    override fun onResume() {
        setState(required = State.STARTED, newState = State.RESUMED)
        set.forEach(Callbacks::onResume)
    }

    override fun onPause() {
        setState(required = State.RESUMED, newState = State.STARTED)
        set.reversed().forEach(Callbacks::onPause)
    }

    override fun onStop() {
        set.reversed().forEach(Callbacks::onStop)
        setState(required = State.STARTED, newState = State.CREATED)
    }

    override fun onDestroy() {
        set.reversed().forEach(Callbacks::onDestroy)
        set = emptySet()
        setState(required = State.CREATED, newState = State.DESTROYED)
    }

    private fun setState(required: State, newState: State) {
        check(_state == required) {
            "Expected lifecycle state $required, actual $_state"
        }

        _state = newState
    }
}
