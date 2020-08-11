package com.arkivanov.mvikotlin.utils.internal

internal const val LOG_TAG = "MVIKotlin"

expect fun logV(text: String)

expect fun logE(text: String)

internal fun String.withLogTag(): String = "[$LOG_TAG]: $this"
