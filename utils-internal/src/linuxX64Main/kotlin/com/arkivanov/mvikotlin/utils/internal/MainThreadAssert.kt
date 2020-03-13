@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.arkivanov.mvikotlin.utils.internal

import platform.posix.fprintf
import platform.posix.pthread_self
import platform.posix.stderr
import kotlin.native.concurrent.AtomicReference

@Suppress("ObjectPropertyName")
private val _mainThreadId = AtomicReference<ULong?>(null)

fun setMainThreadId(id: ULong) {
    if (!_mainThreadId.compareAndSet(null, id)) {
        throw IllegalStateException("Main thread id can be set only once")
    }
}

private fun ensureMainThreadId(): ULong {
    var id: ULong
    var errorMessage: String?
    while (true) {
        errorMessage = null

        val savedId = _mainThreadId.value
        if (savedId != null) {
            id = savedId
            break
        }

        id = pthread_self()
        errorMessage = "Main thread id is not set, current thread is considered as main: $id"

        if (_mainThreadId.compareAndSet(null, id)) {
            break
        }
    }

    errorMessage?.also {
        fprintf(stderr, it)
    }

    return id
}

internal actual val isMainThread: Boolean get() = pthread_self() == ensureMainThreadId()

internal actual val currentThreadDescription: String get() = "Thread(id=${pthread_self()})"
