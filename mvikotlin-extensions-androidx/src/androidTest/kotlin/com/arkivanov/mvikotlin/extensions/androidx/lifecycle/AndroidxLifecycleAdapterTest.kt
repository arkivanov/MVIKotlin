package com.arkivanov.mvikotlin.extensions.androidx.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks.Event.ON_CREATE
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks.Event.ON_DESTROY
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks.Event.ON_PAUSE
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks.Event.ON_RESUME
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks.Event.ON_START
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks.Event.ON_STOP
import org.junit.Test

class AndroidxLifecycleAdapterTest {

    private val androidLifecycle = TestAndroidLifecycle()
    private val lifecycle = AndroidxLifecycleAdapter(androidLifecycle)
    private val callbacks = TestLifecycleCallbacks()
    private val owner = LifecycleOwner { androidLifecycle }

    @Test
    fun calls_callbacks() {
        lifecycle.subscribe(callbacks)
        androidLifecycle.onCreate(owner)
        androidLifecycle.onStart(owner)
        androidLifecycle.onResume(owner)
        androidLifecycle.onPause(owner)
        androidLifecycle.onStop(owner)
        androidLifecycle.onDestroy(owner)

        callbacks.assertEvents(ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY)
    }

    @Test
    fun does_not_call_unsubscribed_callbacks() {
        lifecycle.subscribe(callbacks)
        lifecycle.unsubscribe(callbacks)
        androidLifecycle.onCreate(owner)
        androidLifecycle.onStart(owner)
        androidLifecycle.onResume(owner)
        androidLifecycle.onPause(owner)
        androidLifecycle.onStop(owner)
        androidLifecycle.onDestroy(owner)

        callbacks.assertNoEvents()
    }
}
