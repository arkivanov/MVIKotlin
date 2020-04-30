package com.arkivanov.mvikotlin.utils.internal

import java.io.Serializable

@Suppress("ArrayInDataClass")
sealed class Value : Serializable {

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
        data class IntArray(val value: kotlin.IntArray?) : Object()
        data class LongArray(val value: kotlin.LongArray?) : Object()
        data class ShortArray(val value: kotlin.ShortArray?) : Object()
        data class ByteArray(val value: kotlin.ByteArray?) : Object()
        data class FloatArray(val value: kotlin.FloatArray?) : Object()
        data class DoubleArray(val value: kotlin.DoubleArray?) : Object()
        data class CharArray(val value: kotlin.CharArray?) : Object()
        data class BooleanArray(val value: kotlin.BooleanArray?) : Object()
        data class Array(val type: kotlin.String, val value: List<Object>?) : Object()
        data class Iterable(val type: kotlin.String, val value: List<Object>?) : Object()
        data class Map(val type: kotlin.String, val value: kotlin.collections.Map<Object, Object>?) : Object()
        data class Other(val type: kotlin.String, val value: kotlin.collections.Map<kotlin.String, Value>?) : Object()
        data class Unparsed(val type: kotlin.String, val value: kotlin.String) : Object()
    }
}
