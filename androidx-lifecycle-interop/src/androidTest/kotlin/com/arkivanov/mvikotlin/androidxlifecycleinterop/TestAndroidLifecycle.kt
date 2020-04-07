package com.arkivanov.mvikotlin.androidxlifecycleinterop

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Lifecycle as AndroidLifecycle

class TestAndroidLifecycle : AndroidLifecycle(), DefaultLifecycleObserver {

    private var state = State.INITIALIZED
    private val observers = ArrayList<LifecycleObserver>()

    override fun addObserver(observer: LifecycleObserver) {
        observers += observer
    }

    override fun removeObserver(observer: LifecycleObserver) {
        observers -= observer
    }

    override fun getCurrentState(): State = state

    override fun onCreate(owner: LifecycleOwner) {
        state = State.CREATED
        forEachObserver { it.onCreate(owner) }
    }

    override fun onStart(owner: LifecycleOwner) {
        state = State.STARTED
        forEachObserver { it.onStart(owner) }
    }

    override fun onResume(owner: LifecycleOwner) {
        state = State.RESUMED
        forEachObserver { it.onResume(owner) }
    }

    override fun onPause(owner: LifecycleOwner) {
        forEachObserver { it.onPause(owner) }
        state = State.STARTED
    }

    override fun onStop(owner: LifecycleOwner) {
        forEachObserver { it.onStop(owner) }
        state = State.CREATED
    }

    override fun onDestroy(owner: LifecycleOwner) {
        forEachObserver { it.onDestroy(owner) }
        state = State.DESTROYED
    }

    private fun forEachObserver(block: (DefaultLifecycleObserver) -> Unit) {
        observers
            .mapNotNull { it as? DefaultLifecycleObserver }
            .forEach(block)
    }
}
