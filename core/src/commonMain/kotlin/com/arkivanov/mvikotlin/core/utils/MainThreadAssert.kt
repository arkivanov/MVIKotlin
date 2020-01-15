package com.arkivanov.mvikotlin.core.utils

internal expect val isMainThread: Boolean
internal expect val currentThreadDescription: String

fun assertOnMainThread() {
    require(isMainThread) {
        "Not on Main thread, current thread is: $currentThreadDescription"
    }
}
