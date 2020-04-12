package com.arkivanov.mvikotlin.logging.utils

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.LoggingMode
import com.arkivanov.mvikotlin.utils.internal.DeepStringMode
import com.arkivanov.mvikotlin.utils.internal.toDeepString

internal fun formatLogText(storeName: String, eventType: StoreEventType, value: Any?, deepStringMode: DeepStringMode): String {
    val valueClassName: String? = value?.let { it::class.simpleName }
    val valueText: String? = value?.toDeepString(deepStringMode, false)

    return if (valueClassName == null) {
        "$storeName ($eventType): $valueText"
    } else {
        "$storeName ($eventType, $valueClassName): $valueText"
    }
}

internal fun formatLogText(storeName: String, eventType: StoreEventType, value: Any?, loggingMode: LoggingMode): String? {
    val deepStringMode = loggingMode.toDeepStringMode() ?: return null

    return formatLogText(storeName, eventType, value, deepStringMode)
}

internal fun LoggingMode.toDeepStringMode(): DeepStringMode? =
    when (this) {
        LoggingMode.DISABLED -> null
        LoggingMode.SHORT -> DeepStringMode.SHORT
        LoggingMode.MEDIUM -> DeepStringMode.MEDIUM
        LoggingMode.FULL -> DeepStringMode.FULL
    }
