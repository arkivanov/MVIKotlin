package com.arkivanov.mvikotlin.core.lifecycle

import com.arkivanov.mvikotlin.core.lifecycle.TestLifecycleCallbacks.Event.ON_CREATE
import com.arkivanov.mvikotlin.core.lifecycle.TestLifecycleCallbacks.Event.ON_DESTROY
import com.arkivanov.mvikotlin.core.lifecycle.TestLifecycleCallbacks.Event.ON_PAUSE
import com.arkivanov.mvikotlin.core.lifecycle.TestLifecycleCallbacks.Event.ON_RESUME
import com.arkivanov.mvikotlin.core.lifecycle.TestLifecycleCallbacks.Event.ON_START
import com.arkivanov.mvikotlin.core.lifecycle.TestLifecycleCallbacks.Event.ON_STOP
import kotlin.test.Test
import kotlin.test.assertEquals

class LifecycleRegistryTest {

    private val lifecycle = LifecycleRegistry()
    private val callbacks1 = TestLifecycleCallbacks()
    private val callbacks2 = TestLifecycleCallbacks()

    @Test
    fun calls_all_callbacks() {
        lifecycle.subscribe(callbacks1)
        lifecycle.subscribe(callbacks2)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onStop()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onDestroy()

        val events = listOf(ON_CREATE, ON_START, ON_STOP, ON_START, ON_RESUME, ON_PAUSE, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY)
        callbacks1.assertEvents(events)
        callbacks2.assertEvents(events)
    }

    @Test
    fun calls_all_callbacks_in_proper_order() {
        val events = ArrayList<String>()
        val callbacks1 = TestLifecycleCallbacks { events += "first-$it" }
        val callbacks2 = TestLifecycleCallbacks { events += "second-$it" }

        lifecycle.subscribe(callbacks1)
        lifecycle.subscribe(callbacks2)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onDestroy()

        assertEquals(
            listOf(
                "first-$ON_CREATE",
                "second-$ON_CREATE",
                "first-$ON_START",
                "second-$ON_START",
                "first-$ON_RESUME",
                "second-$ON_RESUME",
                "second-$ON_PAUSE",
                "first-$ON_PAUSE",
                "second-$ON_STOP",
                "first-$ON_STOP",
                "second-$ON_DESTROY",
                "first-$ON_DESTROY"
            ),
            events
        )
    }

    @Test
    fun drives_callback_to_created_state_WHEN_created_and_subscribed() {
        lifecycle.onCreate()
        lifecycle.subscribe(callbacks1)

        callbacks1.assertEvents(ON_CREATE)
    }

    @Test
    fun drives_callback_to_started_state_WHEN_started_and_subscribed() {
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.subscribe(callbacks1)

        callbacks1.assertEvents(ON_CREATE, ON_START)
    }

    @Test
    fun drives_callback_to_resumed_state_WHEN_resumed_and_subscribed() {
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.subscribe(callbacks1)

        callbacks1.assertEvents(ON_CREATE, ON_START, ON_RESUME)
    }

    @Test
    fun drives_callback_to_started_state_WHEN_paused_and_subscribed() {
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.subscribe(callbacks1)

        callbacks1.assertEvents(ON_CREATE, ON_START)
    }

    @Test
    fun drives_callback_to_created_state_WHEN_stopped_and_subscribed() {
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.subscribe(callbacks1)

        callbacks1.assertEvents(ON_CREATE)
    }

    @Test
    fun does_not_call_callback_WHEN_destroyed_and_subscribed() {
        lifecycle.onCreate()
        lifecycle.onDestroy()
        lifecycle.subscribe(callbacks1)

        callbacks1.assertNoEvents()
    }

    @Test
    fun does_not_call_subscribed_callbacks_WHEN_driving_new_subscriber() {
        lifecycle.subscribe(callbacks1)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        callbacks1.reset()
        lifecycle.subscribe(callbacks2)

        callbacks1.assertNoEvents()
    }

    @Test
    fun does_not_call_unsubscribed_callbacks() {
        lifecycle.subscribe(callbacks1)
        lifecycle.unsubscribe(callbacks1)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.subscribe(callbacks2)
        lifecycle.unsubscribe(callbacks2)
        callbacks2.reset()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onDestroy()

        callbacks1.assertNoEvents()
        callbacks2.assertNoEvents()
    }
}
