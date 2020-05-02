package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.utils.formatLogText

internal inline fun LoggerWrapper.log(eventType: StoreEventType? = null, text: () -> String) {
    if (isEnabled && ((eventType == null) || (eventType in eventTypes))) {
        log(text())
    }
}

internal fun LoggerWrapper.log(storeName: String, eventType: StoreEventType, value: Any?) {
    log(eventType = eventType) {
        formatLogText(storeName = storeName, eventType = eventType, value = value)
    }
}
