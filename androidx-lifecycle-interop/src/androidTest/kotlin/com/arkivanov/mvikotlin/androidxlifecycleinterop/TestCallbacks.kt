package com.arkivanov.mvikotlin.androidxlifecycleinterop

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import kotlin.test.assertEquals

class TestLifecycleCallbacks : Lifecycle.Callbacks {

    private val events = ArrayList<Event>()

    override fun onCreate() {
        events += Event.ON_CREATE
    }

    override fun onStart() {
        events += Event.ON_START
    }

    override fun onResume() {
        events += Event.ON_RESUME
    }

    override fun onPause() {
        events += Event.ON_PAUSE
    }

    override fun onStop() {
        events += Event.ON_STOP
    }

    override fun onDestroy() {
        events += Event.ON_DESTROY
    }

    fun assertEvents(events: List<Event>) {
        assertEquals(events, this.events)
    }

    fun assertEvents(vararg events: Event) {
        assertEvents(events.toList())
    }

    fun assertNoEvents() {
        assertEvents()
    }

    fun reset() {
        events.clear()
    }

    enum class Event {
        ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY
    }
}
