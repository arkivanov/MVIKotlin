package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
class SerializerTest {

    @Test
    fun WHEN_onNext_called_synchronously_THEN_emits_all_values() {
        var values by atomic(emptyList<Int>())
        val serializer = Serializer<Int> { values = values + it }.freeze()

        serializer.onNext(0)
        serializer.onNext(1)
        serializer.onNext(2)

        assertEquals(listOf(0, 1, 2), values)
    }

    @Test
    fun WHEN_onNext_called_recursively_THEN_emits_all_values_non_recursively() {
        var values by atomic(emptyList<Int>())
        var serializer by atomic<Serializer<Int>>()

        serializer =
            Serializer<Int> {
                when (it) {
                    0 -> serializer?.onNext(1)
                    1 -> serializer?.onNext(2)
                }
                values = values + it
            }.freeze()

        serializer?.onNext(0)

        assertEquals(listOf(0, 1, 2), values)
    }
}
