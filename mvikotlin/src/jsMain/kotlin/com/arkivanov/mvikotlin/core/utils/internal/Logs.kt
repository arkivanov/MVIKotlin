package com.arkivanov.mvikotlin.core.utils.internal

@InternalMviKotlinApi
actual fun logV(text: String) {
    console.log(text.withLogTag())
}

@InternalMviKotlinApi
actual fun logE(text: String) {
    console.error(text.withLogTag())
}
