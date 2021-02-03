package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<ParsedValue>() {

    @Test
    fun writes_and_reads_Value_Primitive_Int() {
        testWriteRead(ParsedValue.Primitive.Int(123))
        testWriteRead(ParsedValue.Primitive.Int(0))
        testWriteRead(ParsedValue.Primitive.Int(Int.MAX_VALUE))
        testWriteRead(ParsedValue.Primitive.Int(Int.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Long() {
        testWriteRead(ParsedValue.Primitive.Long(123L))
        testWriteRead(ParsedValue.Primitive.Long(0L))
        testWriteRead(ParsedValue.Primitive.Long(Long.MAX_VALUE))
        testWriteRead(ParsedValue.Primitive.Long(Long.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Short() {
        testWriteRead(ParsedValue.Primitive.Short(123))
        testWriteRead(ParsedValue.Primitive.Short(0))
        testWriteRead(ParsedValue.Primitive.Short(Short.MAX_VALUE))
        testWriteRead(ParsedValue.Primitive.Short(Short.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Byte() {
        testWriteRead(ParsedValue.Primitive.Byte(123))
        testWriteRead(ParsedValue.Primitive.Byte(0))
        testWriteRead(ParsedValue.Primitive.Byte(Byte.MAX_VALUE))
        testWriteRead(ParsedValue.Primitive.Byte(Byte.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Float() {
        testWriteRead(ParsedValue.Primitive.Float(123F))
        testWriteRead(ParsedValue.Primitive.Float(0F))
        testWriteRead(ParsedValue.Primitive.Float(Float.MAX_VALUE))
        testWriteRead(ParsedValue.Primitive.Float(Float.MIN_VALUE))
        testWriteRead(ParsedValue.Primitive.Float(Float.POSITIVE_INFINITY))
        testWriteRead(ParsedValue.Primitive.Float(Float.NEGATIVE_INFINITY))
        testWriteRead(ParsedValue.Primitive.Float(Float.NaN))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Double() {
        testWriteRead(ParsedValue.Primitive.Double(123.0))
        testWriteRead(ParsedValue.Primitive.Double(0.0))
        testWriteRead(ParsedValue.Primitive.Double(Double.MAX_VALUE))
        testWriteRead(ParsedValue.Primitive.Double(Double.MIN_VALUE))
        testWriteRead(ParsedValue.Primitive.Double(Double.POSITIVE_INFINITY))
        testWriteRead(ParsedValue.Primitive.Double(Double.NEGATIVE_INFINITY))
        testWriteRead(ParsedValue.Primitive.Double(Double.NaN))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Char() {
        testWriteRead(ParsedValue.Primitive.Char('x'))
        testWriteRead(ParsedValue.Primitive.Char(0.toChar()))
        testWriteRead(ParsedValue.Primitive.Char(Char.MAX_VALUE))
        testWriteRead(ParsedValue.Primitive.Char(Char.MIN_VALUE))
        testWriteRead(ParsedValue.Primitive.Char(Char.MAX_SURROGATE))
        testWriteRead(ParsedValue.Primitive.Char(Char.MIN_SURROGATE))
        testWriteRead(ParsedValue.Primitive.Char(Char.MAX_HIGH_SURROGATE))
        testWriteRead(ParsedValue.Primitive.Char(Char.MIN_HIGH_SURROGATE))
        testWriteRead(ParsedValue.Primitive.Char(Char.MAX_LOW_SURROGATE))
        testWriteRead(ParsedValue.Primitive.Char(Char.MIN_LOW_SURROGATE))
    }

    @Test
    fun writes_and_reads_Value_Primitive_Boolean() {
        testWriteRead(ParsedValue.Primitive.Boolean(true))
        testWriteRead(ParsedValue.Primitive.Boolean(false))
    }

    @Test
    fun writes_and_reads_Value_Object_Int() {
        testWriteRead(ParsedValue.Object.Int(null))
        testWriteRead(ParsedValue.Object.Int(123))
        testWriteRead(ParsedValue.Object.Int(0))
        testWriteRead(ParsedValue.Object.Int(Int.MAX_VALUE))
        testWriteRead(ParsedValue.Object.Int(Int.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Object_Long() {
        testWriteRead(ParsedValue.Object.Long(null))
        testWriteRead(ParsedValue.Object.Long(123L))
        testWriteRead(ParsedValue.Object.Long(0L))
        testWriteRead(ParsedValue.Object.Long(Long.MAX_VALUE))
        testWriteRead(ParsedValue.Object.Long(Long.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Object_Short() {
        testWriteRead(ParsedValue.Object.Short(null))
        testWriteRead(ParsedValue.Object.Short(123))
        testWriteRead(ParsedValue.Object.Short(0))
        testWriteRead(ParsedValue.Object.Short(Short.MAX_VALUE))
        testWriteRead(ParsedValue.Object.Short(Short.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Object_Byte() {
        testWriteRead(ParsedValue.Object.Byte(null))
        testWriteRead(ParsedValue.Object.Byte(123))
        testWriteRead(ParsedValue.Object.Byte(0))
        testWriteRead(ParsedValue.Object.Byte(Byte.MAX_VALUE))
        testWriteRead(ParsedValue.Object.Byte(Byte.MIN_VALUE))
    }

    @Test
    fun writes_and_reads_Value_Object_Float() {
        testWriteRead(ParsedValue.Object.Float(null))
        testWriteRead(ParsedValue.Object.Float(123F))
        testWriteRead(ParsedValue.Object.Float(0F))
        testWriteRead(ParsedValue.Object.Float(Float.MAX_VALUE))
        testWriteRead(ParsedValue.Object.Float(Float.MIN_VALUE))
        testWriteRead(ParsedValue.Object.Float(Float.POSITIVE_INFINITY))
        testWriteRead(ParsedValue.Object.Float(Float.NEGATIVE_INFINITY))
        testWriteRead(ParsedValue.Object.Float(Float.NaN))
    }

    @Test
    fun writes_and_reads_Value_Object_Double() {
        testWriteRead(ParsedValue.Object.Double(null))
        testWriteRead(ParsedValue.Object.Double(123.0))
        testWriteRead(ParsedValue.Object.Double(0.0))
        testWriteRead(ParsedValue.Object.Double(Double.MAX_VALUE))
        testWriteRead(ParsedValue.Object.Double(Double.MIN_VALUE))
        testWriteRead(ParsedValue.Object.Double(Double.POSITIVE_INFINITY))
        testWriteRead(ParsedValue.Object.Double(Double.NEGATIVE_INFINITY))
        testWriteRead(ParsedValue.Object.Double(Double.NaN))
    }

    @Test
    fun writes_and_reads_Value_Object_Char() {
        testWriteRead(ParsedValue.Object.Char(null))
        testWriteRead(ParsedValue.Object.Char('x'))
        testWriteRead(ParsedValue.Object.Char(0.toChar()))
        testWriteRead(ParsedValue.Object.Char(Char.MAX_VALUE))
        testWriteRead(ParsedValue.Object.Char(Char.MIN_VALUE))
        testWriteRead(ParsedValue.Object.Char(Char.MAX_SURROGATE))
        testWriteRead(ParsedValue.Object.Char(Char.MIN_SURROGATE))
        testWriteRead(ParsedValue.Object.Char(Char.MAX_HIGH_SURROGATE))
        testWriteRead(ParsedValue.Object.Char(Char.MIN_HIGH_SURROGATE))
        testWriteRead(ParsedValue.Object.Char(Char.MAX_LOW_SURROGATE))
        testWriteRead(ParsedValue.Object.Char(Char.MIN_LOW_SURROGATE))
    }

    @Test
    fun writes_and_reads_Value_Object_Boolean() {
        testWriteRead(ParsedValue.Object.Boolean(null))
        testWriteRead(ParsedValue.Object.Boolean(true))
        testWriteRead(ParsedValue.Object.Boolean(false))
    }

    @Test
    fun writes_and_reads_Value_Object_String() {
        testWriteRead(ParsedValue.Object.String(null))
        testWriteRead(ParsedValue.Object.String(""))
        testWriteRead(ParsedValue.Object.String("value"))
    }

    @Test
    fun writes_and_reads_Value_Object_IntArray() {
        testWriteRead(ParsedValue.Object.IntArray(intArrayOf(0, Int.MAX_VALUE, Int.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_LongArray() {
        testWriteRead(ParsedValue.Object.LongArray(longArrayOf(0L, Long.MAX_VALUE, Long.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_ShortArray() {
        testWriteRead(ParsedValue.Object.ShortArray(shortArrayOf(0, Short.MAX_VALUE, Short.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_ByteArray() {
        testWriteRead(ParsedValue.Object.ByteArray(byteArrayOf(0, Byte.MAX_VALUE, Byte.MIN_VALUE)))
    }

    @Test
    fun writes_and_reads_Value_Object_FloatArray() {
        testWriteRead(
            ParsedValue.Object.FloatArray(
                floatArrayOf(0F, Float.MAX_VALUE, Float.MIN_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN)
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_DoubleArray() {
        testWriteRead(
            ParsedValue.Object.DoubleArray(
                doubleArrayOf(0.0, Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN)
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_CharArray() {
        testWriteRead(
            ParsedValue.Object.CharArray(
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
        testWriteRead(ParsedValue.Object.BooleanArray(booleanArrayOf(false, true)))
    }

    @Test
    fun writes_and_reads_Value_Object_Array_Null() {
        testWriteRead(ParsedValue.Object.Array(type = "Array<T>", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Array_NotNull() {
        testWriteRead(
            ParsedValue.Object.Array(
                type = "Array<T>",
                value = listOf(
                    ParsedValue.Object.String("string1"),
                    ParsedValue.Object.String("string2"),
                    ParsedValue.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    ParsedValue.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Iterable_Null() {
        testWriteRead(ParsedValue.Object.Iterable(type = "Iterable<T>", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Iterable_NotNull() {
        testWriteRead(
            ParsedValue.Object.Iterable(
                type = "Iterable<T>",
                value = listOf(
                    ParsedValue.Object.String("string1"),
                    ParsedValue.Object.String("string2"),
                    ParsedValue.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    ParsedValue.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Map_Null() {
        testWriteRead(ParsedValue.Object.Map(type = "Map<K,V>", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Map_NotNull() {
        testWriteRead(
            ParsedValue.Object.Map(
                type = "Map<K,V>",
                value = mapOf(
                    ParsedValue.Object.String("obj1") to ParsedValue.Object.String("string1"),
                    ParsedValue.Object.String("obj2") to ParsedValue.Object.String("string2"),
                    ParsedValue.Object.String("obj3") to ParsedValue.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    ParsedValue.Object.String("obj4") to ParsedValue.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Other_Null() {
        testWriteRead(ParsedValue.Object.Other(type = "otherType", value = null))
    }

    @Test
    fun writes_and_reads_Value_Object_Other_NotNull() {
        testWriteRead(
            ParsedValue.Object.Other(
                type = "otherType",
                value = mapOf(
                    "obj1" to ParsedValue.Object.String("string1"),
                    "obj2" to ParsedValue.Object.String("string2"),
                    "obj3" to ParsedValue.Object.Unparsed(type = "unparsedType1", value = "unparsedValue1"),
                    "obj4" to ParsedValue.Object.Unparsed(type = "unparsedType2", value = "unparsedValue2")
                )
            )
        )
    }

    @Test
    fun writes_and_reads_Value_Object_Unparsed() {
        testWriteRead(ParsedValue.Object.Unparsed(type = "unparsedType", value = "unparsedValue"))
    }

    override fun DataWriter.writeObject(obj: ParsedValue) {
        writeValue(obj)
    }

    override fun DataReader.readObject(): ParsedValue = readValue()
}
