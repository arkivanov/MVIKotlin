package com.arkivanov.mvikotlin.utils.internal

import com.badoo.reaktive.utils.atomic.AtomicBoolean

@Suppress("ObjectPropertyName")
private val _isAssertOnMainThreadEnabled = AtomicBoolean(true)
var isAssertOnMainThreadEnabled: Boolean
    get() = _isAssertOnMainThreadEnabled.value
    set(value) {
        _isAssertOnMainThreadEnabled.value = value
    }

internal expect val isMainThread: Boolean

internal expect val currentThreadDescription: String

fun assertOnMainThread() {
    if (isAssertOnMainThreadEnabled) {
        require(isMainThread) {
            "Not on Main thread, current thread is: $currentThreadDescription"
        }
    }
}
