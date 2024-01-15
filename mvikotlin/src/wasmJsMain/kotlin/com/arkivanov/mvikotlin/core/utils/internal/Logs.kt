package com.arkivanov.mvikotlin.core.utils.internal

@InternalMviKotlinApi
actual fun logV(text: String) {
    consoleLog(text.withLogTag())
}

@Suppress("UNUSED_PARAMETER")
private fun consoleLog(text: String) {
    js("console.log(text)")
}

@InternalMviKotlinApi
actual fun logE(text: String) {
    consoleError(text.withLogTag())
}

@Suppress("UNUSED_PARAMETER")
private fun consoleError(text: String) {
    js("console.error(text)")
}
