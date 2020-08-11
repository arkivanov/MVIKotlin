package com.arkivanov.mvikotlin.utils.internal

import platform.Foundation.NSThread

internal actual fun getMainThreadId(): MainThreadId? = MainThreadId()

internal actual fun isMainThread(mainThreadId: MainThreadId): Boolean = NSThread.isMainThread()

internal actual fun getCurrentThreadDescription(): String = "Thread(name=${NSThread.currentThread().name()})"

internal actual class MainThreadId
