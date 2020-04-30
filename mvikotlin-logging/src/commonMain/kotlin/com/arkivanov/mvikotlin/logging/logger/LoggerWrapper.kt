package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType

internal interface LoggerWrapper {

    val isEnabled: Boolean
    val eventTypes: Set<StoreEventType>

    fun log(text: String)
}
