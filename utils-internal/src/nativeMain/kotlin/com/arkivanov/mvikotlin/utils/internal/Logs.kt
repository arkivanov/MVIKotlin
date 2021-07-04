package com.arkivanov.mvikotlin.utils.internal

import platform.posix.perror

actual fun logV(text: String) {
    println(text.withLogTag())
}

actual fun logE(text: String) {
    perror(text.withLogTag())
}
