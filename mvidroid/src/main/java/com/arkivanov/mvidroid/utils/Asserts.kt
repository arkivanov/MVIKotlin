package com.arkivanov.mvidroid.utils

private val mainThread: Thread = Thread.currentThread()

internal fun assertOnMainThread() {
    Thread.currentThread().takeUnless { it === mainThread }?.also {
        throw RuntimeException("Not on Main thread, current thread: $it")
    }
}
