package com.arkivanov.mvikotlin.core.binder

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

class BinderAttachToLifecycleTest {

    private val binder = TestBinder()
    private val lifecycle = LifecycleRegistry()

    @Test
    fun calls_binder_correctly_WHEN_attachTo_with_CREATE_DESTROY() {
        binder.attachTo(lifecycle, BinderLifecycleMode.CREATE_DESTROY)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onDestroy()

        binder.assertEvents(BinderEvent.START to Lifecycle.State.CREATED, BinderEvent.STOP to Lifecycle.State.DESTROYED)
    }

    @Test
    fun calls_binder_correctly_WHEN_attachTo_with_START_STOP() {
        binder.attachTo(lifecycle, BinderLifecycleMode.START_STOP)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onDestroy()

        binder.assertEvents(
            BinderEvent.START to Lifecycle.State.STARTED,
            BinderEvent.STOP to Lifecycle.State.CREATED,
            BinderEvent.START to Lifecycle.State.STARTED,
            BinderEvent.STOP to Lifecycle.State.CREATED
        )
    }

    @Test
    fun calls_binder_correctly_WHEN_attachTo_with_RESUME_PAUSE() {
        binder.attachTo(lifecycle, BinderLifecycleMode.RESUME_PAUSE)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onResume()
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onDestroy()

        binder.assertEvents(
            BinderEvent.START to Lifecycle.State.RESUMED,
            BinderEvent.STOP to Lifecycle.State.STARTED,
            BinderEvent.START to Lifecycle.State.RESUMED,
            BinderEvent.STOP to Lifecycle.State.STARTED
        )
    }

    private inner class TestBinder : Binder {
        private val events: MutableList<Pair<BinderEvent, Lifecycle.State>> = ArrayList()

        override fun start() {
            events += BinderEvent.START to lifecycle.state
        }

        override fun stop() {
            events += BinderEvent.STOP to lifecycle.state
        }

        fun assertEvents(vararg events: Pair<BinderEvent, Lifecycle.State>) {
            assertEquals(events.toList(), this.events)
        }
    }

    enum class BinderEvent {
        START, STOP
    }
}
