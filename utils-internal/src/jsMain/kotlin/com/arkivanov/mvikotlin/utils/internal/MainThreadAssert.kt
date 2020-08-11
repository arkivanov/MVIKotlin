package com.arkivanov.mvikotlin.utils.internal

internal actual fun getMainThreadId(): MainThreadId? = MainThreadId()

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = true

internal actual fun getCurrentThreadDescription(): String = "Main thread"

internal actual class MainThreadId
