package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.LoggingMode

internal interface LoggerWrapper {

    val mode: LoggingMode
    val eventTypes: Set<StoreEventType>

    fun log(text: String)
}
