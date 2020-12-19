package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType

/**
 * A default implementation of the [LogFormatter]
 *
 * @param valueLengthLimit a length limit for [Store][com.arkivanov.mvikotlin.core.store.Store] event values,
 * 0 to disable value logging
 */
class DefaultLogFormatter(
    private val valueLengthLimit: Int = DEFAULT_VALUE_LENGTH_LIMIT
) : LogFormatter {

    override fun format(storeName: String, eventType: StoreEventType, value: Any?): String? {
        val valueClassName: String? = value?.let { it::class.simpleName }
        val valueText: String? = if (valueLengthLimit > 0) value?.toString()?.take(valueLengthLimit) else null

        return StringBuilder()
            .appendDefaultLogText(
                storeName = storeName,
                eventType = eventType,
                valueClassName = valueClassName,
                valueText = valueText
            )
            .toString()
    }

    private fun StringBuilder.appendDefaultLogText(
        storeName: String,
        eventType: StoreEventType,
        valueClassName: String?,
        valueText: String?
    ): StringBuilder {
        append(storeName)
        append(" (")
        append(eventType)

        if (valueClassName != null) {
            append(", ")
            append(valueClassName)
        }

        append(')')

        if (valueText != null) {
            append(": ")
            append(valueText)
        }

        return this
    }

    companion object {
        const val DEFAULT_VALUE_LENGTH_LIMIT: Int = 256
    }
}
