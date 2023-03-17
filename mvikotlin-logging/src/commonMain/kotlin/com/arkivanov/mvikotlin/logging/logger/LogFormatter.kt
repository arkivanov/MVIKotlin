package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType

/**
 * Represents MVIKotlin log formatter
 */
interface LogFormatter {

    /**
     * Formats a log string for [Store][com.arkivanov.mvikotlin.core.store.Store] event
     *
     * @param storeName a name of the [Store][com.arkivanov.mvikotlin.core.store.Store]
     * @param eventType a type of the [Store][com.arkivanov.mvikotlin.core.store.Store] event,
     * see [StoreEventType] for more information
     * @param value a value of the [Store][com.arkivanov.mvikotlin.core.store.Store] event
     * @return a formatted string for logging or `null` if the event should not be logged
     */
    fun format(storeName: String, eventType: StoreEventType, value: Any?): String?
}
