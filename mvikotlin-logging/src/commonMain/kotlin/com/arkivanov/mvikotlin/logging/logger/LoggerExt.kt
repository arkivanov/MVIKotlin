package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.LoggingMode
import com.arkivanov.mvikotlin.logging.utils.formatLogText
import com.arkivanov.mvikotlin.logging.utils.toDeepStringMode

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
