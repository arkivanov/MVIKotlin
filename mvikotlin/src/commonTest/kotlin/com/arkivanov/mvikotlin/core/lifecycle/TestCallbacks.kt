package com.arkivanov.mvikotlin.core.lifecycle

import kotlin.test.assertEquals

class TestLifecycleCallbacks(
    private val onEvent: (Event) -> Unit = {}
) : Lifecycle.Callbacks {

    private val events = ArrayList<Event>()

    override fun onCreate() {
        onEvent(Event.ON_CREATE)
    }

    override fun onStart() {
        onEvent(Event.ON_START)
    }

    override fun onResume() {
        onEvent(Event.ON_RESUME)
    }

    override fun onPause() {
        onEvent(Event.ON_PAUSE)
    }

    override fun onStop() {
        onEvent(Event.ON_STOP)
    }

    override fun onDestroy() {
        onEvent(Event.ON_DESTROY)
    }

    private fun onEvent(event: Event) {
        events += event
        onEvent.invoke(event)
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
