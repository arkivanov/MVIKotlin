package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.logger.LogFormatter
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TestLogger(
    private val formatter: LogFormatter,
    private val storeName: String
) : Logger {

    private var logs by atomic(emptyList<String>())

    override fun log(text: String) {
        this.logs += text
    }

    fun assertLoggedText(text: String) {
        assertTrue(text in logs)
    }

    fun assertNoLoggedText(text: String) {
        assertFalse(text in logs)
    }

    fun assertLoggedEvent(eventType: StoreEventType, value: Any?) {
        assertLoggedText(requireNotNull(formatter.format(storeName = storeName, eventType = eventType, value = value)))
    }

    fun assertNoLogs() {
        assertEquals(emptyList(), logs)
    }
}
