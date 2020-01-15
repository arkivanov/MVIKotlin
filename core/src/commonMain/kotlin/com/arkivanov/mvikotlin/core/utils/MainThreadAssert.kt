package com.arkivanov.mvikotlin.core.utils

import com.arkivanov.mvikotlin.core.annotations.MainThread

internal expect val isMainThread: Boolean
internal expect val currentThreadDescription: String

@MainThread
fun assertOnMainThread() {
    require(isMainThread) {
        "Not on Main thread, current thread is: $currentThreadDescription"
    }
}
