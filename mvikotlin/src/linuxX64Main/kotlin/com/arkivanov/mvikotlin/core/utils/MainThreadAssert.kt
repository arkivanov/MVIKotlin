@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.arkivanov.mvikotlin.core.utils

import platform.posix.pthread_self
import kotlin.concurrent.AtomicReference

private val mainThreadIdRef = AtomicReference<ULong?>(null)

fun setMainThreadId(id: ULong) {
    if (!mainThreadIdRef.compareAndSet(null, id)) {
        error("Main thread id is already set")
    }
}

internal actual fun getMainThreadId(): MainThreadId? = mainThreadIdRef.value?.let(::MainThreadId)

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = mainThreadId.id == pthread_self()

internal actual fun getCurrentThreadDescription(): String = "Thread(id=${pthread_self()})"

internal actual class MainThreadId(val id: ULong)
