@file:JvmName("MainThreadAssert")

package com.arkivanov.mvikotlin.core.utils

import java.util.concurrent.atomic.AtomicReference

@Suppress("ObjectPropertyName")
private var _mainThreadId = AtomicReference<Long?>(null)

fun setMainThreadId(id: Long) {
    if (!_mainThreadId.compareAndSet(null, id)) {
        throw IllegalStateException("Main thread id can be set only once")
    }
}

private fun ensureMainThreadId(): Long {
    var id: Long
    var errorMessage: String?
    while (true) {
        errorMessage = null

        val savedId = _mainThreadId.get()
        if (savedId != null) {
            id = savedId
            break
        }

        id = Thread.currentThread().id
        errorMessage = "Main thread id is not set, current thread is considered as main: $id"

        if (_mainThreadId.compareAndSet(null, id)) {
            break
        }
    }

    errorMessage?.also {
        System.err.println(it)
    }

    return id
}

internal actual val isMainThread: Boolean get() = Thread.currentThread().id == ensureMainThreadId()

internal actual val currentThreadDescription: String get() = Thread.currentThread().toString()
