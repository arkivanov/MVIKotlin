package com.arkivanov.mvidroid.utils

import android.os.Looper
import android.support.annotation.MainThread

private val mainThreadId: Long by lazy {
    try {
        Looper.getMainLooper().thread.id
    } catch (ignored: Exception) {
        0L
    }
}

@MainThread
fun assertOnMainThread() {
    Thread
        .currentThread()
        .takeIf { (mainThreadId > 0L) && (it.id != mainThreadId) }
        ?.also { throw RuntimeException("Not on Main thread, current thread: $it") }
}
