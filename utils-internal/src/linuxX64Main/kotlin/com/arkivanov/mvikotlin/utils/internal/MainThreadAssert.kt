@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.arkivanov.mvikotlin.utils.internal

import platform.posix.pthread_self
import kotlin.native.concurrent.AtomicReference

@Suppress("ObjectPropertyName")
private val mainThreadIdRef = AtomicReference<ULong?>(null)

fun setMainThreadId(id: ULong) {
    if (!mainThreadIdRef.compareAndSet(null, id)) {
        throw IllegalStateException("Main thread id can be set only once")
    }
}

internal actual fun getMainThreadId(): MainThreadId? = mainThreadIdRef.value?.let(::MainThreadId)

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = mainThreadId.id == pthread_self()

internal actual fun getCurrentThreadDescription(): String = "Thread(id=${pthread_self()})"

internal actual class MainThreadId(val id: ULong)
