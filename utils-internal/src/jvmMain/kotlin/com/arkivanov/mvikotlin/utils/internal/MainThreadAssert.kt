@file:JvmName("MainThreadAssert")

package com.arkivanov.mvikotlin.utils.internal

import java.util.concurrent.atomic.AtomicReference

private val mainThreadIdRef = AtomicReference<Long?>(null)

fun setMainThreadId(id: Long) {
    if (!mainThreadIdRef.compareAndSet(null, id)) {
        error("Main thread id is already set")
    }
}

internal actual fun getMainThreadId(): MainThreadId? = mainThreadIdRef.get()?.let(::MainThreadId)

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = mainThreadId.id == Thread.currentThread().id

internal actual fun getCurrentThreadDescription(): String = Thread.currentThread().toString()

internal actual class MainThreadId(val id: Long)
