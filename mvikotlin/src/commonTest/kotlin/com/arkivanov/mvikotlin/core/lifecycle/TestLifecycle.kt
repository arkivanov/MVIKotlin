package com.arkivanov.mvikotlin.core.lifecycle

class TestLifecycle : Lifecycle, Lifecycle.Callbacks {

    private var set = emptySet<Lifecycle.Callbacks>()
    override var state: Lifecycle.State = Lifecycle.State.INITIALIZED

    override fun subscribe(callbacks: Lifecycle.Callbacks) {
        set = set + callbacks
    }

    override fun unsubscribe(callbacks: Lifecycle.Callbacks) {
        set = set - callbacks
    }

    override fun onCreate() {
        state = Lifecycle.State.CREATED
        set.forEach(Lifecycle.Callbacks::onCreate)
    }

    override fun onStart() {
        state = Lifecycle.State.STARTED
        set.forEach(Lifecycle.Callbacks::onStart)
    }

    override fun onResume() {
        state = Lifecycle.State.RESUMED
        set.forEach(Lifecycle.Callbacks::onResume)
    }

    override fun onPause() {
        set.forEach(Lifecycle.Callbacks::onPause)
        state = Lifecycle.State.STARTED
    }

    override fun onStop() {
        set.forEach(Lifecycle.Callbacks::onStop)
        state = Lifecycle.State.CREATED
    }

    override fun onDestroy() {
        set.forEach(Lifecycle.Callbacks::onDestroy)
        state = Lifecycle.State.DESTROYED
    }
}
