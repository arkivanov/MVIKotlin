@file:JvmName("MainThreadAssert")

package com.arkivanov.mvikotlin.utils.internal

import java.util.concurrent.atomic.AtomicReference

@Suppress("ObjectPropertyName")
private val mainThreadIdRef = AtomicReference<Long?>(null)

fun setMainThreadId(id: Long) {
    if (!mainThreadIdRef.compareAndSet(null, id)) {
        throw IllegalStateException("Main thread id can be set only once")
    }
}

internal actual fun getMainThreadId(): MainThreadId? = mainThreadIdRef.get()?.let(::MainThreadId)

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = mainThreadId.id == Thread.currentThread().id

internal actual fun getCurrentThreadDescription(): String = Thread.currentThread().toString()

internal actual class MainThreadId(val id: Long)
