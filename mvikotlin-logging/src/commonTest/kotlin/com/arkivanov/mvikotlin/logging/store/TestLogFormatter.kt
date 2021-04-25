package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.logger.LogFormatter

internal class TestLogFormatter : LogFormatter {

    override fun format(storeName: String, eventType: StoreEventType, value: Any?): String =
        "$storeName;$eventType;$value"
}
