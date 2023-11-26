package com.arkivanov.mvikotlin.core.utils.internal

import com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.withLogTag

@InternalMviKotlinApi
actual fun logV(text: String) {
    console.log(text.withLogTag())
}

@InternalMviKotlinApi
actual fun logE(text: String) {
    console.error(text.withLogTag())
}
