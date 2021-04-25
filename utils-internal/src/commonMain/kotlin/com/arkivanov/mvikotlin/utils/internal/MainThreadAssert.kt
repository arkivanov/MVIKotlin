package com.arkivanov.mvikotlin.utils.internal

var isAssertOnMainThreadEnabled: Boolean by atomic(true)

private val mainThreadIdRef = atomic<MainThreadIdHolder?>(null)

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

fun isMainThread(): Boolean {
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
