package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import kotlin.BooleanArray as KBooleanArray
import kotlin.ByteArray as KByteArray
import kotlin.CharArray as KCharArray
import kotlin.DoubleArray as KDoubleArray
import kotlin.FloatArray as KFloatArray
import kotlin.Int as KInt
import kotlin.Long as KLong
import kotlin.Short as KShort
import kotlin.Byte as KByte
import kotlin.Float as KFloat
import kotlin.Double as KDouble
import kotlin.Char as KChar
import kotlin.Boolean as KBoolean
import kotlin.IntArray as KIntArray
import kotlin.LongArray as KLongArray
import kotlin.ShortArray as KShortArray
import kotlin.String as KString
import kotlin.collections.Map as KMap

sealed class ParsedValue {

    sealed class Primitive : ParsedValue() {
        data class Int(val value: KInt) : Primitive()
        data class Long(val value: KLong) : Primitive()
        data class Short(val value: KShort) : Primitive()
        data class Byte(val value: KByte) : Primitive()
        data class Float(val value: KFloat) : Primitive()
        data class Double(val value: KDouble) : Primitive()
        data class Char(val value: KChar) : Primitive()
        data class Boolean(val value: KBoolean) : Primitive()
    }

    sealed class Object : ParsedValue() {
        data class Int(val value: KInt?) : Object()
        data class Long(val value: KLong?) : Object()
        data class Short(val value: KShort?) : Object()
        data class Byte(val value: KByte?) : Object()
        data class Float(val value: KFloat?) : Object()
        data class Double(val value: KDouble?) : Object()
        data class Char(val value: KChar?) : Object()
        data class Boolean(val value: KBoolean?) : Object()
        data class String(val value: KString?) : Object()

        data class IntArray(val value: kotlin.IntArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is IntArray) && eq(value, other.value, KIntArray::contentEquals)
            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class LongArray(val value: KLongArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is LongArray) && eq(value, other.value, KLongArray::contentEquals)
            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class ShortArray(val value: KShortArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is ShortArray) && eq(value, other.value, KShortArray::contentEquals)
            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class ByteArray(val value: KByteArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is ByteArray) && eq(value, other.value, KByteArray::contentEquals)
            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class FloatArray(val value: KFloatArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is FloatArray) && eq(value, other.value, KFloatArray::contentEquals)
            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class DoubleArray(val value: KDoubleArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is DoubleArray) && eq(value, other.value, KDoubleArray::contentEquals)
            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class CharArray(val value: KCharArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is CharArray) && eq(value, other.value, KCharArray::contentEquals)
            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class BooleanArray(val value: KBooleanArray?) : Object() {
            override fun equals(other: Any?): KBoolean = (other is BooleanArray) && eq(value, other.value, KBooleanArray::contentEquals)

            override fun hashCode(): KInt = value?.contentHashCode() ?: 0
        }

        data class Array(val type: KString, val value: List<Object>?) : Object()
        data class Iterable(val type: KString, val value: List<Object>?) : Object()
        data class Map(val type: KString, val value: KMap<Object, Object>?) : Object()
        data class Other(val type: KString, val value: KMap<KString, ParsedValue>?) : Object()
        data class Unparsed(val type: KString, val value: KString) : Object()
    }

    private companion object {
        private inline fun <T : Any> eq(a: T?, b: T?, comparator: (T, T) -> KBoolean): KBoolean =
            (a === b) || ((a != null) && (b != null) && comparator(a, b))
    }
}
