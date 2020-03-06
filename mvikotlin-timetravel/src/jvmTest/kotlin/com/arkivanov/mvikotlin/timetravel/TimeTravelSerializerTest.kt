package com.arkivanov.mvikotlin.timetravel

import com.arkivanov.mvikotlin.core.store.StoreEventType
import org.junit.Test
import kotlin.test.assertEquals

class TimeTravelSerializerTest {

    private val serializer = TimeTravelSerializer()

    @Test
    fun test_serialize_deserialize() {
        val events =
            listOf(
                TimeTravelEvent(
                    storeName = "store1",
                    type = StoreEventType.INTENT,
                    value = 100,
                    state = "state1"
                ),
                TimeTravelEvent(
                    storeName = "store2",
                    type = StoreEventType.STATE,
                    value = 200,
                    state = "state2"
                )
            )

        val result = serializer.deserialize(serializer.serialize(events))

        assertEquals(events, result)
    }
}
