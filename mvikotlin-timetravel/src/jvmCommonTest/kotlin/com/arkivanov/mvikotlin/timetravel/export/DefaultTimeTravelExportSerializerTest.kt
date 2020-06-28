package com.arkivanov.mvikotlin.timetravel.export

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import org.junit.Test
import kotlin.test.assertEquals

class DefaultTimeTravelExportSerializerTest {

    @Test
    fun serialize_deserialize() {
        val serializer = DefaultTimeTravelExportSerializer
        val export = TimeTravelExport(recordedEvents = getEventsForTest(), unusedStoreStates = getUnusedStoreStatesForTest())

        val payload = serializer.serialize(export) as TimeTravelExportSerializer.Result.Success
        val result = serializer.deserialize(payload.data) as TimeTravelExportSerializer.Result.Success

        assertEquals(export, result.data)
    }

    private fun getEventsForTest(): List<TimeTravelEvent> =
        List(1000) { index ->
            TimeTravelEvent(
                id = index.toLong(),
                storeName = "Store$index",
                type = StoreEventType.values().let { it[index % it.size] },
                value = index,
                state = index
            )
        }

    private fun getUnusedStoreStatesForTest(): Map<String, Any> =
        List(1000) { it }
            .associateBy { "Store$it" }
}
