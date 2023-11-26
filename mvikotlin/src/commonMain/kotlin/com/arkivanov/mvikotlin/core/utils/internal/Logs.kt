package com.arkivanov.mvikotlin.core.utils.internal

internal const val LOG_TAG = "MVIKotlin"

@InternalMviKotlinApi
expect fun logV(text: String)

@InternalMviKotlinApi
expect fun logE(text: String)

@InternalMviKotlinApi
internal fun String.withLogTag(): String = "[$LOG_TAG]: $this"
