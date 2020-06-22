package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import kotlin.BooleanArray as KBooleanArray
import kotlin.ByteArray as KByteArray
import kotlin.CharArray as KCharArray
import kotlin.DoubleArray as KDoubleArray
import kotlin.FloatArray as KFloatArray
import kotlin.IntArray as KIntArray
import kotlin.LongArray as KLongArray
import kotlin.ShortArray as KShortArray
import kotlin.String as KString
import kotlin.collections.Map as KMap

sealed class Value {

    sealed class Primitive : Value() {
        data class Int(val value: kotlin.Int) : Primitive()
        data class Long(val value: kotlin.Long) : Primitive()
        data class Short(val value: kotlin.Short) : Primitive()
        data class Byte(val value: kotlin.Byte) : Primitive()
        data class Float(val value: kotlin.Float) : Primitive()
        data class Double(val value: kotlin.Double) : Primitive()
        data class Char(val value: kotlin.Char) : Primitive()
        data class Boolean(val value: kotlin.Boolean) : Primitive()
    }

    sealed class Object : Value() {
        data class String(val value: kotlin.String?) : Object()

        data class IntArray(val value: kotlin.IntArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is IntArray) && eq(value, other.value, KIntArray::contentEquals)
            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class LongArray(val value: KLongArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is LongArray) && eq(value, other.value, KLongArray::contentEquals)
            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class ShortArray(val value: KShortArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is ShortArray) && eq(value, other.value, KShortArray::contentEquals)
            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class ByteArray(val value: KByteArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is ByteArray) && eq(value, other.value, KByteArray::contentEquals)
            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class FloatArray(val value: KFloatArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is FloatArray) && eq(value, other.value, KFloatArray::contentEquals)
            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class DoubleArray(val value: KDoubleArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is DoubleArray) && eq(value, other.value, KDoubleArray::contentEquals)
            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class CharArray(val value: KCharArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is CharArray) && eq(value, other.value, KCharArray::contentEquals)
            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class BooleanArray(val value: KBooleanArray?) : Object() {
            override fun equals(other: Any?): Boolean = (other is BooleanArray) && eq(value, other.value, KBooleanArray::contentEquals)

            override fun hashCode(): Int = value?.contentHashCode() ?: 0
        }

        data class Array(val type: KString, val value: List<Object>?) : Object()
        data class Iterable(val type: KString, val value: List<Object>?) : Object()
        data class Map(val type: KString, val value: KMap<Object, Object>?) : Object()
        data class Other(val type: KString, val value: KMap<KString, Value>?) : Object()
        data class Unparsed(val type: KString, val value: KString) : Object()
    }

    private companion object {
        private inline fun <T : Any> eq(a: T?, b: T?, comparator: (T, T) -> Boolean): Boolean =
            (a === b) || ((a != null) && (b != null) && comparator(a, b))
    }
}
