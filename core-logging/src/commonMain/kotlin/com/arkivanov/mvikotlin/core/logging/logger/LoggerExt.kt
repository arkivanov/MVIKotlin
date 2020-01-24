package com.arkivanov.mvikotlin.core.logging.logger

import com.arkivanov.mvikotlin.core.logging.LoggingMode
import com.arkivanov.mvikotlin.core.logging.utils.formatLogText
import com.arkivanov.mvikotlin.core.logging.utils.toDeepStringMode
import com.arkivanov.mvikotlin.core.store.StoreEventType

internal inline fun Logger.log(loggingMode: LoggingMode, text: () -> String) {
    if (loggingMode != LoggingMode.DISABLED) {
        log(text())
    }
}

internal fun Logger.log(loggingMode: LoggingMode, storeName: String, eventType: StoreEventType, value: Any?) {
    loggingMode
        .toDeepStringMode()
        ?.let { formatLogText(storeName, eventType, value, it) }
        ?.also(::log)
}
