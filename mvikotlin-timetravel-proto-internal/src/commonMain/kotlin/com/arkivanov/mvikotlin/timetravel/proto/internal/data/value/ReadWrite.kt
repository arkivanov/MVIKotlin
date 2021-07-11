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

internal fun DataWriter.writeValue(value: ParsedValue) {
    when (value) {
        is ParsedValue.Primitive -> {
            writeEnum(Type.PRIMITIVE)
            writeValuePrimitive(value)
        }

        is ParsedValue.Object -> {
            writeEnum(Type.OBJECT)
            writeValueObject(value)
        }
    }.let {}
}

private fun DataWriter.writeValuePrimitive(value: ParsedValue.Primitive) {
    when (value) {
        is ParsedValue.Primitive.Int -> writeTyped(PrimitiveType.INT) { writeInt(value.value) }
        is ParsedValue.Primitive.Long -> writeTyped(PrimitiveType.LONG) { writeLong(value.value) }
        is ParsedValue.Primitive.Short -> writeTyped(PrimitiveType.SHORT) { writeShort(value.value) }
        is ParsedValue.Primitive.Byte -> writeTyped(PrimitiveType.BYTE) { writeByte(value.value) }
        is ParsedValue.Primitive.Float -> writeTyped(PrimitiveType.FLOAT) { writeFloat(value.value) }
        is ParsedValue.Primitive.Double -> writeTyped(PrimitiveType.DOUBLE) { writeDouble(value.value) }
        is ParsedValue.Primitive.Char -> writeTyped(PrimitiveType.CHAR) { writeChar(value.value) }
        is ParsedValue.Primitive.Boolean -> writeTyped(PrimitiveType.BOOLEAN) { writeBoolean(value.value) }
    }.let {}
}

private fun DataWriter.writeValueObject(value: ParsedValue.Object) {
    when (value) {
        is ParsedValue.Object.Int -> writeTyped(ObjectType.INT) { writeObject(value.value, DataWriter::writeInt) }
        is ParsedValue.Object.Long -> writeTyped(ObjectType.LONG) { writeObject(value.value, DataWriter::writeLong) }
        is ParsedValue.Object.Short -> writeTyped(ObjectType.SHORT) { writeObject(value.value, DataWriter::writeShort) }
        is ParsedValue.Object.Byte -> writeTyped(ObjectType.BYTE) { writeObject(value.value, DataWriter::writeByte) }
        is ParsedValue.Object.Float -> writeTyped(ObjectType.FLOAT) { writeObject(value.value, DataWriter::writeFloat) }
        is ParsedValue.Object.Double -> writeTyped(ObjectType.DOUBLE) { writeObject(value.value, DataWriter::writeDouble) }
        is ParsedValue.Object.Char -> writeTyped(ObjectType.CHAR) { writeObject(value.value, DataWriter::writeChar) }
        is ParsedValue.Object.Boolean -> writeTyped(ObjectType.BOOLEAN) { writeObject(value.value, DataWriter::writeBoolean) }
        is ParsedValue.Object.String -> writeTyped(ObjectType.STRING) { writeString(value.value) }
        is ParsedValue.Object.IntArray -> writeTyped(ObjectType.INT_ARRAY) { writeIntArray(value.value) }
        is ParsedValue.Object.LongArray -> writeTyped(ObjectType.LONG_ARRAY) { writeLongArray(value.value) }
        is ParsedValue.Object.ShortArray -> writeTyped(ObjectType.SHORT_ARRAY) { writeShortArray(value.value) }
        is ParsedValue.Object.ByteArray -> writeTyped(ObjectType.BYTE_ARRAY) { writeByteArray(value.value) }
        is ParsedValue.Object.FloatArray -> writeTyped(ObjectType.FLOAT_ARRAY) { writeFloatArray(value.value) }
        is ParsedValue.Object.DoubleArray -> writeTyped(ObjectType.DOUBLE_ARRAY) { writeDoubleArray(value.value) }
        is ParsedValue.Object.CharArray -> writeTyped(ObjectType.CHAR_ARRAY) { writeCharArray(value.value) }
        is ParsedValue.Object.BooleanArray -> writeTyped(ObjectType.BOOLEAN_ARRAY) { writeBooleanArray(value.value) }

        is ParsedValue.Object.Array ->
            writeTyped(ObjectType.ARRAY) {
                writeString(value.type)
                writeCollection(value.value) {
                    writeValueObject(it)
                }
            }

        is ParsedValue.Object.Iterable ->
            writeTyped(ObjectType.ITERABLE) {
                writeString(value.type)
                writeCollection(value.value) {
                    writeValueObject(it)
                }
            }

        is ParsedValue.Object.Map ->
            writeTyped(ObjectType.MAP) {
                writeString(value.type)
                writeMap(map = value.value, writeKey = { writeValueObject(it) }, writeValue = { writeValueObject(it) })
            }

        is ParsedValue.Object.Other ->
            writeTyped(ObjectType.OTHER) {
                writeString(value.type)
                writeMap(map = value.value, writeKey = { writeString(it) }, writeValue = { writeValue(it) })
            }

        is ParsedValue.Object.Unparsed ->
            writeTyped(ObjectType.UNPARSED) {
                writeString(value.type)
                writeString(value.value)
            }
    }.let {}
}

private inline fun <T : Enum<*>> DataWriter.writeTyped(type: T, block: DataWriter.() -> Unit) {
    writeEnum(type)
    block()
}

private inline fun <T : Any> DataWriter.writeObject(value: T?, block: DataWriter.(T) -> Unit) {
    if (value == null) {
        writeByte(0)
    } else {
        writeByte(1)
        block(value)
    }
}

//endregion Write

//region Read

internal fun DataReader.readValue(): ParsedValue =
    when (readEnum<Type>()) {
        Type.PRIMITIVE -> readValuePrimitive()
        Type.OBJECT -> readValueObject()
    }

private fun DataReader.readValuePrimitive(): ParsedValue.Primitive =
    when (readEnum<PrimitiveType>()) {
        PrimitiveType.INT -> ParsedValue.Primitive.Int(readInt())
        PrimitiveType.LONG -> ParsedValue.Primitive.Long(readLong())
        PrimitiveType.SHORT -> ParsedValue.Primitive.Short(readShort())
        PrimitiveType.BYTE -> ParsedValue.Primitive.Byte(readByte())
        PrimitiveType.FLOAT -> ParsedValue.Primitive.Float(readFloat())
        PrimitiveType.DOUBLE -> ParsedValue.Primitive.Double(readDouble())
        PrimitiveType.CHAR -> ParsedValue.Primitive.Char(readChar())
        PrimitiveType.BOOLEAN -> ParsedValue.Primitive.Boolean(readBoolean())
    }

private fun DataReader.readValueObject(): ParsedValue.Object =
    when (readEnum<ObjectType>()) {
        ObjectType.INT -> ParsedValue.Object.Int(readObject(DataReader::readInt))
        ObjectType.LONG -> ParsedValue.Object.Long(readObject(DataReader::readLong))
        ObjectType.SHORT -> ParsedValue.Object.Short(readObject(DataReader::readShort))
        ObjectType.BYTE -> ParsedValue.Object.Byte(readObject(DataReader::readByte))
        ObjectType.FLOAT -> ParsedValue.Object.Float(readObject(DataReader::readFloat))
        ObjectType.DOUBLE -> ParsedValue.Object.Double(readObject(DataReader::readDouble))
        ObjectType.CHAR -> ParsedValue.Object.Char(readObject(DataReader::readChar))
        ObjectType.BOOLEAN -> ParsedValue.Object.Boolean(readObject(DataReader::readBoolean))
        ObjectType.STRING -> ParsedValue.Object.String(readString())
        ObjectType.INT_ARRAY -> ParsedValue.Object.IntArray(readIntArray())
        ObjectType.LONG_ARRAY -> ParsedValue.Object.LongArray(readLongArray())
        ObjectType.SHORT_ARRAY -> ParsedValue.Object.ShortArray(readShortArray())
        ObjectType.BYTE_ARRAY -> ParsedValue.Object.ByteArray(readByteArray())
        ObjectType.FLOAT_ARRAY -> ParsedValue.Object.FloatArray(readFloatArray())
        ObjectType.DOUBLE_ARRAY -> ParsedValue.Object.DoubleArray(readDoubleArray())
        ObjectType.CHAR_ARRAY -> ParsedValue.Object.CharArray(readCharArray())
        ObjectType.BOOLEAN_ARRAY -> ParsedValue.Object.BooleanArray(readBooleanArray())
        ObjectType.ARRAY -> ParsedValue.Object.Array(type = readString()!!, value = readList { readValueObject() })
        ObjectType.ITERABLE -> ParsedValue.Object.Iterable(type = readString()!!, value = readList { readValueObject() })

        ObjectType.MAP ->
            ParsedValue.Object.Map(
                type = readString()!!,
                value = readMap(readKey = { readValueObject() }, readValue = { readValueObject() })
            )

        ObjectType.OTHER ->
            ParsedValue.Object.Other(
                type = readString()!!,
                value = readMap(readKey = { readString()!! }, readValue = { readValue() })
            )

        ObjectType.UNPARSED -> ParsedValue.Object.Unparsed(type = readString()!!, value = readString()!!)
    }

private inline fun <T : Any> DataReader.readObject(block: DataReader.() -> T): T? =
    if (readByte().toInt() == 0) null else block()

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
    INT,
    LONG,
    SHORT,
    BYTE,
    FLOAT,
    DOUBLE,
    CHAR,
    BOOLEAN,
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
