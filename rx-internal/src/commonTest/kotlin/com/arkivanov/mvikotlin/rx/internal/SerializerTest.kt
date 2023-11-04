package com.arkivanov.mvikotlin.rx.internal

import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
class SerializerTest {

    @Test
    fun WHEN_onNext_called_synchronously_THEN_emits_all_values() {
        val values = ArrayList<Int>()
        val serializer = Serializer<Int> { values += it }

        serializer.onNext(0)
        serializer.onNext(1)
        serializer.onNext(2)

        assertEquals(listOf(0, 1, 2), values)
    }

    @Test
    fun WHEN_onNext_called_recursively_THEN_emits_all_values_non_recursively() {
        val values = ArrayList<Int>()
        var serializer: Serializer<Int>? = null

        serializer =
            Serializer {
                when (it) {
                    0 -> serializer?.onNext(1)
                    1 -> serializer?.onNext(2)
                }
                values += it
            }

        serializer.onNext(0)

        assertEquals(listOf(0, 1, 2), values)
    }
}
