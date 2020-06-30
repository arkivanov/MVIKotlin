package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType

internal fun LoggerWrapper.log(storeName: String, eventType: StoreEventType, value: Any?) {
    format(storeName, eventType, value)
        ?.also(::log)
}
