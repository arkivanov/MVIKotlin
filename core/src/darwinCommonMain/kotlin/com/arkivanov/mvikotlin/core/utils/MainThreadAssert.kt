package com.arkivanov.mvikotlin.core.utils

import platform.Foundation.NSThread

internal actual val isMainThread: Boolean get() = NSThread.isMainThread()

internal actual val currentThreadDescription: String get() = "Thread(name=${NSThread.currentThread().name()})"
