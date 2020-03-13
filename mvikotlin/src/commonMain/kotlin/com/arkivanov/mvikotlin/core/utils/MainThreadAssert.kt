package com.arkivanov.mvikotlin.core.utils

import com.arkivanov.mvikotlin.core.annotations.MainThread

var isAssertOnMainThreadEnabled: Boolean
    get() = com.arkivanov.mvikotlin.utils.internal.isAssertOnMainThreadEnabled
    set(value) {
        com.arkivanov.mvikotlin.utils.internal.isAssertOnMainThreadEnabled = value
    }

@MainThread
fun assertOnMainThread() {
    com.arkivanov.mvikotlin.utils.internal.assertOnMainThread()
}
