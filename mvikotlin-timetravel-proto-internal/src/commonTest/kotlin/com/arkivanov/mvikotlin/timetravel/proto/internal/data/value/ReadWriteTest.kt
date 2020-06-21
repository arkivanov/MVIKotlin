package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<Value>() {

    @Test
    fun writes_and_reads_Value_Primitive_Int() {
        testWriteRead(Value.Primitive.Int(123))
        testWriteRead(Value.Primitive.Int(0))
        testWriteRead(Value.Primitive.Int(Int.MAX_VALUE))
        testWriteRead(Value.Primitive.Int(Int.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Long() {
        testWriteRead(Value.Primitive.Long(123L))
        testWriteRead(Value.Primitive.Long(0L))
        testWriteRead(Value.Primitive.Long(Long.MAX_VALUE))
        testWriteRead(Value.Primitive.Long(Long.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Short() {
        testWriteRead(Value.Primitive.Short(123))
        testWriteRead(Value.Primitive.Short(0))
        testWriteRead(Value.Primitive.Short(Short.MAX_VALUE))
        testWriteRead(Value.Primitive.Short(Short.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Byte() {
        testWriteRead(Value.Primitive.Byte(123))
        testWriteRead(Value.Primitive.Byte(0))
        testWriteRead(Value.Primitive.Byte(Byte.MAX_VALUE))
        testWriteRead(Value.Primitive.Byte(Byte.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Float() {
        testWriteRead(Value.Primitive.Float(123F))
        testWriteRead(Value.Primitive.Float(0F))
        testWriteRead(Value.Primitive.Float(Float.MAX_VALUE))
        testWriteRead(Value.Primitive.Float(Float.MIN_VALUE))
        testWriteRead(Value.Primitive.Float(Float.POSITIVE_INFINITY))
        testWriteRead(Value.Primitive.Float(Float.NEGATIVE_INFINITY))
        testWriteRead(Value.Primitive.Float(Float.NaN))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Double() {
        testWriteRead(Value.Primitive.Double(123.0))
        testWriteRead(Value.Primitive.Double(0.0))
        testWriteRead(Value.Primitive.Double(Double.MAX_VALUE))
        testWriteRead(Value.Primitive.Double(Double.MIN_VALUE))
        testWriteRead(Value.Primitive.Double(Double.POSITIVE_INFINITY))
        testWriteRead(Value.Primitive.Double(Double.NEGATIVE_INFINITY))
        testWriteRead(Value.Primitive.Double(Double.NaN))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Char() {
        testWriteRead(Value.Primitive.Char('x'))
        testWriteRead(Value.Primitive.Char(0.toChar()))
        testWriteRead(Value.Primitive.Char(Char.MAX_VALUE))
        testWriteRead(Value.Primitive.Char(Char.MIN_VALUE))
        testWriteRead(Value.Primitive.Char(Char.MAX_SURROGATE))
        testWriteRead(Value.Primitive.Char(Char.MIN_SURROGATE))
        testWriteRead(Value.Primitive.Char(Char.MAX_HIGH_SURROGATE))
        testWriteRead(Value.Primitive.Char(Char.MIN_HIGH_SURROGATE))
        testWriteRead(Value.Primitive.Char(Char.MAX_LOW_SURROGATE))
        testWriteRead(Value.Primitive.Char(Char.MIN_LOW_SURROGATE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Boolean() {
        testWriteRead(Value.Primitive.Boolean(true))
        testWriteRead(Value.Primitive.Boolean(false))
    }

    @Test
    fun writes_and_reads_Value_Object_String() {
        testWriteRead(Value.Object.String(null))
        testWriteRead(Value.Object.String(""))
        testWriteRead(Value.Object.String("value"))
    }

    @Test
    fun writes_and_reads_Value_Object_IntArray() {
        testWriteRead(Value.Object.IntArray(intArrayOf(0, Int.MAX_VALUE, Int.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_LongArray() {
        testWriteRead(Value.Object.LongArray(longArrayOf(0L, Long.MAX_VALUE, Long.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_ShortArray() {
        testWriteRead(Value.Object.ShortArray(shortArrayOf(0, Short.MAX_VALUE, Short.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_ByteArray() {
        testWriteRead(Value.Object.ByteArray(byteArrayOf(0, Byte.MAX_VALUE, Byte.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_FloatArray() {
        testWriteRead(
            Value.Object.FloatArray(
                floatArrayOf(0F, Float.MAX_VALUE, Float.MIN_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN)
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_DoubleArray() {
        testWriteRead(
            Value.Object.DoubleArray(
                doubleArrayOf(0.0, Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN)
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_CharArray() {
        testWriteRead(
            Value.Object.CharArray(
                charArrayOf(
                    0.toChar(),
                    Char.MAX_VALUE,
                    Char.MIN_VALUE,
                    Char.MAX_SURROGATE,
                    Char.MIN_SURROGATE,
                    Char.MAX_HIGH_SURROGATE,
                    Char.MAX_LOW_SURROGATE,
                    Char.MIN_HIGH_SURROGATE,
                    Char.MIN_LOW_SURROGATE
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_BooleanArray() {
        testWriteRead(Value.Object.BooleanArray(booleanArrayOf(false, true)))
    }

    @Test
    fun writes_and_reads_Value_Object_Array_Null() {
        testWriteRead(Value.Object.Array(type = "Array<T>", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Array_NotNull() {
        testWriteRead(
            Value.Object.Array(
                type = "Array<T>",
                value = listOf(
                    Value.Object.String("string1"),
                    Value.Object.String("string2"),
                    Value.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    Value.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Iterable_Null() {
        testWriteRead(Value.Object.Iterable(type = "Iterable<T>", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Iterable_NotNull() {
        testWriteRead(
            Value.Object.Iterable(
                type = "Iterable<T>",
                value = listOf(
                    Value.Object.String("string1"),
                    Value.Object.String("string2"),
                    Value.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    Value.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Map_Null() {
        testWriteRead(Value.Object.Map(type = "Map<K,V>", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Map_NotNull() {
        testWriteRead(
            Value.Object.Map(
                type = "Map<K,V>",
                value = mapOf(
                    Value.Object.String("obj1") to Value.Object.String("string1"),
                    Value.Object.String("obj2") to Value.Object.String("string2"),
                    Value.Object.String("obj3") to Value.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    Value.Object.String("obj4") to Value.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Other_Null() {
        testWriteRead(Value.Object.Other(type = "otherType", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Other_NotNull() {
        testWriteRead(
            Value.Object.Other(
                type = "otherType",
                value = mapOf(
                    "obj1" to Value.Object.String("string1"),
                    "obj2" to Value.Object.String("string2"),
                    "obj3" to Value.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    "obj4" to Value.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Unparsed() {
        testWriteRead(Value.Object.Unparsed(type = "unparsedType", value = "unparsedValue"))
    }

    override fun DataWriter.writeObject(obj: Value) {
        writeValue(obj)
    }

    override fun DataReader.readObject(): Value = readValue()
}
