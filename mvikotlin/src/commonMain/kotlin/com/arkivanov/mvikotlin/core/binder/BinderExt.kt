package com.arkivanov.mvikotlin.core.binder

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnCreateDestroy
import com.arkivanov.mvikotlin.core.lifecycle.doOnResumePause
import com.arkivanov.mvikotlin.core.lifecycle.doOnStartStop

/**
 * Attaches the [Binder] to the provided [Lifecycle] using the [BinderLifecycleMode].
 * Depending on the [BinderLifecycleMode] will subscribe to appropriate [Lifecycle] callbacks
 * and will call corresponding [Binder.start] and [Binder.stop] methods when notified.
 *
 * @receiver a [Binder] to be controlled by the [Lifecycle]
 * @param lifecycle a [Lifecycle] that will provide lifecycle events
 * @param mode a [BinderLifecycleMode] to be used when subscribing for lifecycle events
 */
fun Binder.attachTo(lifecycle: Lifecycle, mode: BinderLifecycleMode): Binder =
    when (mode) {
        BinderLifecycleMode.CREATE_DESTROY -> lifecycle.doOnCreateDestroy(onCreate = ::start, onDestroy = ::stop)
        BinderLifecycleMode.START_STOP -> lifecycle.doOnStartStop(onStart = ::start, onStop = ::stop)
        BinderLifecycleMode.RESUME_PAUSE -> lifecycle.doOnResumePause(onResume = ::start, onPause = ::stop)
    }.let { this }
