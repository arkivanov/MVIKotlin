package com.arkivanov.mvikotlin.logging.utils

import com.arkivanov.mvikotlin.core.store.StoreEventType

internal fun formatLogText(storeName: String, eventType: StoreEventType, value: Any?): String {
    val valueClassName: String? = value?.let { it::class.simpleName }
    val valueText: String? = value?.toString()

    return if (valueClassName == null) {
        "$storeName ($eventType): $valueText"
    } else {
        "$storeName ($eventType, $valueClassName): $valueText"
    }
}
