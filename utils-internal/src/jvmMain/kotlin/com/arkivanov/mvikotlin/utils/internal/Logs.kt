@file:JvmName("Logs")
package com.arkivanov.mvikotlin.utils.internal

actual fun logV(text: String) {
    println(text.withLogTag())
}

actual fun logE(text: String) {
    System.err.println(text.withLogTag())
}
