package com.arkivanov.mvidroid.utils

import android.os.Looper

private val mainThreadId: Long by lazy {
    try {
        Looper.getMainLooper().thread.id
    } catch (e: Exception) {
        0L
    }
}

internal fun assertOnMainThread() {
    Thread.currentThread().takeIf { (mainThreadId > 0L) && (it.id != mainThreadId) }?.let {
        throw RuntimeException("Not on Main thread, current thread: $it")
    }
}
