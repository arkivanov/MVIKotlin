package com.arkivanov.mvikotlin.utils.internal

actual fun Any.toDeepString(mode: DeepStringMode, format: Boolean): String {
    val maxLength =
        when (mode) {
            DeepStringMode.SHORT -> 64
            DeepStringMode.MEDIUM -> 256
            DeepStringMode.FULL -> Int.MAX_VALUE
        }

    val string = toString()

    return if (string.length > maxLength) string.take(maxLength) else string
}
