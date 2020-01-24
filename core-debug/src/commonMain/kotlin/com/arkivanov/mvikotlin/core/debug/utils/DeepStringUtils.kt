package com.arkivanov.mvikotlin.core.debug.utils

internal expect fun Any.toDeepString(mode: DeepStringMode, format: Boolean): String

internal enum class DeepStringMode {

    SHORT, MEDIUM, FULL
}
