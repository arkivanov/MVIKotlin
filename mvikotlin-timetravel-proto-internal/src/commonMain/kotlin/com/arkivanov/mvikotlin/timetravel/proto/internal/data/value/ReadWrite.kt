package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readBoolean
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readBooleanArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readByteArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readChar
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readCharArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readDouble
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readDoubleArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readFloat
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readFloatArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readInt
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readIntArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readList
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readLong
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readLongArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readMap
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readShort
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readShortArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readString
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeBoolean
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeBooleanArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeByteArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeChar
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeCharArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeCollection
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeDouble
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeDoubleArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeFloat
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeFloatArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeInt
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeIntArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeLong
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeLongArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeMap
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeShort
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeShortArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeString

//region Write

internal fun DataWriter.writeValue(value: Value) {
    when (value) {
        is Value.Primitive -> {
            writeEnum(Type.PRIMITIVE)
            writeValuePrimitive(value)
        }

        is Value.Object -> {
            writeEnum(Type.OBJECT)
            writeValueObject(value)
        }
    }.let {}
}

private fun DataWriter.writeValuePrimitive(value: Value.Primitive) {
    when (value) {
        is Value.Primitive.Int -> writeTyped(PrimitiveType.INT) { writeInt(value.value) }
        is Value.Primitive.Long -> writeTyped(PrimitiveType.LONG) { writeLong(value.value) }
        is Value.Primitive.Short -> writeTyped(PrimitiveType.SHORT) { writeShort(value.value) }
        is Value.Primitive.Byte -> writeTyped(PrimitiveType.BYTE) { writeByte(value.value) }
        is Value.Primitive.Float -> writeTyped(PrimitiveType.FLOAT) { writeFloat(value.value) }
        is Value.Primitive.Double -> writeTyped(PrimitiveType.DOUBLE) { writeDouble(value.value) }
        is Value.Primitive.Char -> writeTyped(PrimitiveType.CHAR) { writeChar(value.value) }
        is Value.Primitive.Boolean -> writeTyped(PrimitiveType.BOOLEAN) { writeBoolean(value.value) }
    }.let {}
}

private fun DataWriter.writeValueObject(value: Value.Object) {
    when (value) {
        is Value.Object.String -> writeTyped(ObjectType.STRING) { writeString(value.value) }
        is Value.Object.IntArray -> writeTyped(ObjectType.INT_ARRAY) { writeIntArray(value.value) }
        is Value.Object.LongArray -> writeTyped(ObjectType.LONG_ARRAY) { writeLongArray(value.value) }
        is Value.Object.ShortArray -> writeTyped(ObjectType.SHORT_ARRAY) { writeShortArray(value.value) }
        is Value.Object.ByteArray -> writeTyped(ObjectType.BYTE_ARRAY) { writeByteArray(value.value) }
        is Value.Object.FloatArray -> writeTyped(ObjectType.FLOAT_ARRAY) { writeFloatArray(value.value) }
        is Value.Object.DoubleArray -> writeTyped(ObjectType.DOUBLE_ARRAY) { writeDoubleArray(value.value) }
        is Value.Object.CharArray -> writeTyped(ObjectType.CHAR_ARRAY) { writeCharArray(value.value) }
        is Value.Object.BooleanArray -> writeTyped(ObjectType.BOOLEAN_ARRAY) { writeBooleanArray(value.value) }

        is Value.Object.Array ->
            writeTyped(ObjectType.ARRAY) {
                writeString(value.type)
                writeCollection(value.value) {
                    writeValueObject(it)
                }
            }

        is Value.Object.Iterable ->
            writeTyped(ObjectType.ITERABLE) {
                writeString(value.type)
                writeCollection(value.value) {
                    writeValueObject(it)
                }
            }

        is Value.Object.Map ->
            writeTyped(ObjectType.MAP) {
                writeString(value.type)
                writeMap(map = value.value, writeKey = { writeValueObject(it) }, writeValue = { writeValueObject(it) })
            }

        is Value.Object.Other ->
            writeTyped(ObjectType.OTHER) {
                writeString(value.type)
                writeMap(map = value.value, writeKey = { writeString(it) }, writeValue = { writeValue(it) })
            }

        is Value.Object.Unparsed ->
            writeTyped(ObjectType.UNPARSED) {
                writeString(value.type)
                writeString(value.value)
            }
    }
}

private inline fun <T : Enum<*>> DataWriter.writeTyped(type: T, block: DataWriter.() -> Unit) {
    writeEnum(type)
    block()
}

//endregion Write

//region Read

internal fun DataReader.readValue(): Value =
    when (readEnum<Type>()) {
        Type.PRIMITIVE -> readValuePrimitive()
        Type.OBJECT -> readValueObject()
    }

private fun DataReader.readValuePrimitive(): Value.Primitive =
    when (readEnum<PrimitiveType>()) {
        PrimitiveType.INT -> Value.Primitive.Int(readInt())
        PrimitiveType.LONG -> Value.Primitive.Long(readLong())
        PrimitiveType.SHORT -> Value.Primitive.Short(readShort())
        PrimitiveType.BYTE -> Value.Primitive.Byte(readByte())
        PrimitiveType.FLOAT -> Value.Primitive.Float(readFloat())
        PrimitiveType.DOUBLE -> Value.Primitive.Double(readDouble())
        PrimitiveType.CHAR -> Value.Primitive.Char(readChar())
        PrimitiveType.BOOLEAN -> Value.Primitive.Boolean(readBoolean())
    }

private fun DataReader.readValueObject(): Value.Object =
    when (readEnum<ObjectType>()) {
        ObjectType.STRING -> Value.Object.String(readString())
        ObjectType.INT_ARRAY -> Value.Object.IntArray(readIntArray())
        ObjectType.LONG_ARRAY -> Value.Object.LongArray(readLongArray())
        ObjectType.SHORT_ARRAY -> Value.Object.ShortArray(readShortArray())
        ObjectType.BYTE_ARRAY -> Value.Object.ByteArray(readByteArray())
        ObjectType.FLOAT_ARRAY -> Value.Object.FloatArray(readFloatArray())
        ObjectType.DOUBLE_ARRAY -> Value.Object.DoubleArray(readDoubleArray())
        ObjectType.CHAR_ARRAY -> Value.Object.CharArray(readCharArray())
        ObjectType.BOOLEAN_ARRAY -> Value.Object.BooleanArray(readBooleanArray())
        ObjectType.ARRAY -> Value.Object.Array(type = readString()!!, value = readList { readValueObject() })
        ObjectType.ITERABLE -> Value.Object.Iterable(type = readString()!!, value = readList { readValueObject() })

        ObjectType.MAP ->
            Value.Object.Map(
                type = readString()!!,
                value = readMap(readKey = { readValueObject() }, readValue = { readValueObject() })
            )

        ObjectType.OTHER ->
            Value.Object.Other(
                type = readString()!!,
                value = readMap(readKey = { readString()!! }, readValue = { readValue() })
            )

        ObjectType.UNPARSED -> Value.Object.Unparsed(type = readString()!!, value = readString()!!)
    }

//endregion

private enum class Type {
    PRIMITIVE, OBJECT
}

private enum class PrimitiveType {
    INT,
    LONG,
    SHORT,
    BYTE,
    FLOAT,
    DOUBLE,
    CHAR,
    BOOLEAN
}

private enum class ObjectType {
    STRING,
    INT_ARRAY,
    LONG_ARRAY,
    SHORT_ARRAY,
    BYTE_ARRAY,
    FLOAT_ARRAY,
    DOUBLE_ARRAY,
    CHAR_ARRAY,
    BOOLEAN_ARRAY,
    ARRAY,
    ITERABLE,
    MAP,
    OTHER,
    UNPARSED
}
