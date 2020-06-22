package com.arkivanov.mvikotlin.timetravel.proto.internal.io

internal fun DataWriter.writeInt(value: Int) {
    writeByte((value and 0xFF).toByte())
    writeByte(((value shr 8) and 0xFF).toByte())
    writeByte(((value shr 16) and 0xFF).toByte())
    writeByte(((value shr 24) and 0xFF).toByte())
}

internal fun DataWriter.writeLong(value: Long) {
    writeByte((value and 0xFF).toByte())
    writeByte(((value shr 8) and 0xFF).toByte())
    writeByte(((value shr 16) and 0xFF).toByte())
    writeByte(((value shr 24) and 0xFF).toByte())
    writeByte(((value shr 32) and 0xFF).toByte())
    writeByte(((value shr 40) and 0xFF).toByte())
    writeByte(((value shr 48) and 0xFF).toByte())
    writeByte(((value shr 56) and 0xFF).toByte())
}

internal fun DataWriter.writeShort(value: Short) {
    writeInt(value.toInt())
}

internal fun DataWriter.writeChar(value: Char) {
    writeInt(value.toInt())
}

internal expect fun DataWriter.writeFloat(value: Float)

internal fun DataWriter.writeDouble(value: Double) {
    writeLong(value.toRawBits())
}

internal fun DataWriter.writeBoolean(value: Boolean) {
    writeByte(if (value) 1 else 0)
}

@OptIn(ExperimentalStdlibApi::class)
internal fun DataWriter.writeString(value: String?) {
    writeByteArray(value?.encodeToByteArray())
}

internal fun DataWriter.writeByteArray(array: ByteArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        if (array.isNotEmpty()) {
            write(array = array)
        }
    }
}

internal fun DataWriter.writeIntArray(array: IntArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        array.forEach(::writeInt)
    }
}

internal fun DataWriter.writeLongArray(array: LongArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        array.forEach(::writeLong)
    }
}

internal fun DataWriter.writeShortArray(array: ShortArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        array.forEach(::writeShort)
    }
}

internal fun DataWriter.writeFloatArray(array: FloatArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        array.forEach(::writeFloat)
    }
}

internal fun DataWriter.writeDoubleArray(array: DoubleArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        array.forEach(::writeDouble)
    }
}

internal fun DataWriter.writeCharArray(array: CharArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        array.forEach(::writeChar)
    }
}

internal fun DataWriter.writeBooleanArray(array: BooleanArray?) {
    if (array == null) {
        writeInt(-1)
    } else {
        writeInt(array.size)
        array.forEach(::writeBoolean)
    }
}

internal inline fun <T> DataWriter.writeCollection(collection: Collection<T>?, writeItem: DataWriter.(T) -> Unit) {
    if (collection == null) {
        writeInt(-1)
    } else {
        writeInt(collection.size)
        collection.forEach {
            writeItem(it)
        }
    }
}

internal inline fun <K, V> DataWriter.writeMap(map: Map<K, V>?, writeKey: DataWriter.(K) -> Unit, writeValue: DataWriter.(V) -> Unit) {
    if (map == null) {
        writeInt(-1)
    } else {
        writeInt(map.size)
        map.forEach { (key, value) ->
            writeKey(key)
            writeValue(value)
        }
    }
}

internal fun <T : Enum<*>> DataWriter.writeEnum(enum: T) {
    writeByte(enum.ordinal.toByte())
}
