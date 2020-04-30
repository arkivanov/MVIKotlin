package com.arkivanov.mvikotlin.timetravel.proto

import com.arkivanov.mvikotlin.utils.internal.Value
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTravelStateUpdateSerializationTest {

    @Test
    fun serializes_all() {
        val written = stateUpdate(TimeTravelEventsUpdate::All)
        val read = writeRead(written)

        assertEquals(written, read)
    }

    @Test
    fun serializes_new() {
        val written = stateUpdate(TimeTravelEventsUpdate::New)
        val read = writeRead(written)

        assertEquals(written, read)
    }

    private inline fun <reified T : Serializable> writeRead(data: T): T {
        val out = ByteArrayOutputStream()
        val objectOut = ObjectOutputStream(out)
        objectOut.writeObject(data)
        objectOut.flush()
        val array = out.toByteArray()
        val dataIn = ObjectInputStream(ByteArrayInputStream(array))

        return dataIn.readObject() as T
    }

    private fun stateUpdate(eventsUpdate: (List<TimeTravelEvent>) -> TimeTravelEventsUpdate): TimeTravelStateUpdate =
        TimeTravelStateUpdate(
            eventsUpdate = eventsUpdate(
                listOf(
                    TimeTravelEvent(
                        id = 1L,
                        storeName = "store1",
                        type = StoreEventType.INTENT,
                        value = value()
                    )
                )
            ),
            selectedEventIndex = 5,
            mode = TimeTravelStateUpdate.Mode.RECORDING
        )

    private fun value(): Value =
        Value.Object.Other(
            type = "type",
            value = mapOf(
                "int" to Value.Primitive.Int(123),
                "long" to Value.Primitive.Long(234L),
                "short" to Value.Primitive.Short(345),
                "byte" to Value.Primitive.Byte(23),
                "float" to Value.Primitive.Float(543F),
                "double" to Value.Primitive.Double(763.0),
                "char" to Value.Primitive.Char('f'),
                "boolean1" to Value.Primitive.Boolean(false),
                "boolean2" to Value.Primitive.Boolean(true),
                "array" to Value.Object.Array("Array<T>", listOf(Value.Object.String("av1"), Value.Object.String("av2"))),
                "iterable" to Value.Object.Iterable("List<T>", listOf(Value.Object.String("lv1"), Value.Object.String("lv2"))),
                "map" to Value.Object.Map(
                    "Map<K,V>",
                    mapOf(
                        Value.Object.String("mk1") to Value.Object.String("mv1"),
                        Value.Object.String("mk2") to Value.Object.String("mv2")
                    )
                ),
                "other" to Value.Object.Other(
                    "Other",
                    mapOf(
                        "on1" to Value.Object.String("ov1"),
                        "on2" to Value.Object.String("ov2")
                    )
                ),
                "unparsed" to Value.Object.Unparsed("Unparsed", "UnparsedValue")
            )
        )
}
