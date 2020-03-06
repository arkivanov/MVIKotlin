package com.arkivanov.mvikotlin.utils.internal

expect fun Any.toDeepString(mode: DeepStringMode, format: Boolean): String

enum class DeepStringMode {

    SHORT, MEDIUM, FULL
}
