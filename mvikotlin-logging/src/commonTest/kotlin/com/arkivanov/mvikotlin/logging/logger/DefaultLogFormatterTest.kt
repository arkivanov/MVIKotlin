package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.core.store.StoreEventType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DefaultLogFormatterTest {

    @Test
    fun message_contains_whole_value_WHEN_valueLengthLimit_not_exceeded() {
        val formatter = DefaultLogFormatter(valueLengthLimit = 10)
        val value = "abcdefghij"

        val result = formatter.format(storeName = "store", eventType = StoreEventType.STATE, value = value)

        assertNotNull(result)
        assertTrue(result.contains(value))
    }

    @Test
    fun message_contains_truncated_value_WHEN_valueLengthLimit_exceeded() {
        val formatter = DefaultLogFormatter(valueLengthLimit = 10)
        val value = "abcdefghijk"

        val result = formatter.format(storeName = "store", eventType = StoreEventType.STATE, value = value)

        assertNotNull(result)
        assertTrue(result.contains(value.take(10)))
    }

    @Test
    fun message_does_not_contain_whole_value_WHEN_valueLengthLimit_exceeded() {
        val formatter = DefaultLogFormatter(valueLengthLimit = 10)
        val value = "abcdefghijk"

        val result = formatter.format(storeName = "store", eventType = StoreEventType.STATE, value = value)

        assertNotNull(result)
        assertFalse(result.contains(value))
    }

    @Test
    fun message_does_not_contain_value_WHEN_valueLengthLimit_is_0() {
        val formatter = DefaultLogFormatter(valueLengthLimit = 0)
        val value = "12345"

        val result = formatter.format(storeName = "store", eventType = StoreEventType.STATE, value = value)

        assertNotNull(result)
        assertFalse(result.any { it in value })
    }
}
