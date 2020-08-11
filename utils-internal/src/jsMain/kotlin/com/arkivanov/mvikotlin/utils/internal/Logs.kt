package com.arkivanov.mvikotlin.utils.internal

actual fun logV(text: String) {
    console.log(text.withLogTag())
}

actual fun logE(text: String) {
    console.error(text.withLogTag())
}
