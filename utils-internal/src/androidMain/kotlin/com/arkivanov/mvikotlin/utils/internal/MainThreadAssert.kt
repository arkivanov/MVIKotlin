@file:JvmName("MainThreadAssert")

package com.arkivanov.mvikotlin.utils.internal

import android.os.Looper

internal actual fun getMainThreadId(): MainThreadId? =
    try {
        MainThreadId(Looper.getMainLooper().thread.id)
    } catch (e: Exception) {
        logE("Unable to determine Main thread id: ${e.message}")
        null
    }

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = mainThreadId.id == Thread.currentThread().id

internal actual fun getCurrentThreadDescription(): String = Thread.currentThread().toString()

internal actual class MainThreadId(val id: Long)
