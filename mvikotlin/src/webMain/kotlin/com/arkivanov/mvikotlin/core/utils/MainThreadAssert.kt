package com.arkivanov.mvikotlin.core.utils

internal actual fun getMainThreadId(): MainThreadId? = MainThreadId()

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = true

internal actual fun getCurrentThreadDescription(): String = "Main thread"

internal actual class MainThreadId
