package com.arkivanov.mvikotlin.core.utils.internal

import com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.withLogTag
import platform.posix.perror

@InternalMviKotlinApi
actual fun logV(text: String) {
    println(text.withLogTag())
}

@InternalMviKotlinApi
actual fun logE(text: String) {
    perror(text.withLogTag())
}
