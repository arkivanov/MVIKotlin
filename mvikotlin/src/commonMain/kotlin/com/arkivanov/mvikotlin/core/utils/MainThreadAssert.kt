package com.arkivanov.mvikotlin.core.utils

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.utils.internal.AtomicRef
import com.arkivanov.mvikotlin.core.utils.internal.atomic
import com.arkivanov.mvikotlin.core.utils.internal.logE
import kotlin.concurrent.Volatile

@Volatile
var isAssertOnMainThreadEnabled: Boolean = true

@MainThread
fun assertOnMainThread() {
    if (isAssertOnMainThreadEnabled) {
        require(isMainThread()) {
            "Not on Main thread, current thread is: ${getCurrentThreadDescription()}"
        }
    }
}

private val mainThreadIdRef = atomic<MainThreadIdHolder?>(null)

internal expect fun getMainThreadId(): MainThreadId?

internal expect fun isMainThread(mainThreadId: MainThreadId): Boolean

internal expect fun getCurrentThreadDescription(): String

internal fun isMainThread(): Boolean {
    if (!isAssertOnMainThreadEnabled) {
        return true
    }

    val mainThreadId =
        mainThreadIdRef.initAndGet {
            val id: MainThreadId? = getMainThreadId()
            if (id == null) {
                logE("Main thread id is undefined, main thread assert is disabled")
            }
            MainThreadIdHolder(id)
        }

    return mainThreadId.id?.let(::isMainThread) ?: true
}

private inline fun <T : Any> AtomicRef<T?>.initAndGet(init: () -> T): T {
    while (true) {
        var v: T? = value
        if (v != null) {
            return v
        }

        v = init()
        if (compareAndSet(null, v)) {
            return v
        }
    }
}

internal expect class MainThreadId

private class MainThreadIdHolder(val id: MainThreadId?)
