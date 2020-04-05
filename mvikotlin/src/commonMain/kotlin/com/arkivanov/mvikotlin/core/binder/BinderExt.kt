package com.arkivanov.mvikotlin.core.binder

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnCreateDestroy
import com.arkivanov.mvikotlin.core.lifecycle.doOnResumePause
import com.arkivanov.mvikotlin.core.lifecycle.doOnStartStop

fun Binder.attachTo(lifecycle: Lifecycle, mode: BinderLifecycleMode): Binder =
    when (mode) {
        BinderLifecycleMode.CREATE_DESTROY -> lifecycle.doOnCreateDestroy(onCreate = ::start, onDestroy = ::stop)
        BinderLifecycleMode.START_STOP -> lifecycle.doOnStartStop(onStart = ::start, onStop = ::stop)
        BinderLifecycleMode.RESUME_PAUSE -> lifecycle.doOnResumePause(onResume = ::start, onPause = ::stop)
    }.let { this }
