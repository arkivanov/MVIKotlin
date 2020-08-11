package com.arkivanov.mvikotlin.utils.internal

import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue

var isAssertOnMainThreadEnabled: Boolean by AtomicBoolean(true)

private val mainThreadIdRef = AtomicReference<MainThreadIdHolder?>(null)

fun assertOnMainThread() {
    if (isAssertOnMainThreadEnabled) {
        require(isMainThread()) {
            "Not on Main thread, current thread is: ${getCurrentThreadDescription()}"
        }
    }
}

internal expect fun getMainThreadId(): MainThreadId?

internal expect fun isMainThread(mainThreadId: MainThreadId): Boolean

internal expect fun getCurrentThreadDescription(): String

private fun isMainThread(): Boolean {
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

private inline fun <T : Any> AtomicReference<T?>.initAndGet(init: () -> T): T {
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
