package com.arkivanov.mvikotlin.core.utils.internal

@InternalMviKotlinApi
actual fun logV(text: String) {
    println(text.withLogTag())
}

@InternalMviKotlinApi
actual fun logE(text: String) {
    println(text.withLogTag())
}
