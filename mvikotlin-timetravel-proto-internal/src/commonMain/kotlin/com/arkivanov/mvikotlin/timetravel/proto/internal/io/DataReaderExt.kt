package com.arkivanov.mvikotlin.timetravel.proto.internal.io

internal fun DataReader.readInt(): Int =
    readByte().toInt().and(0xFF) or
        readByte().toInt().and(0xFF).shl(8) or
        readByte().toInt().and(0xFF).shl(16) or
        readByte().toInt().and(0xFF).shl(24)

internal fun DataReader.readLong(): Long =
    readByte().toLong().and(0xFF) or
        readByte().toLong().and(0xFF).shl(8) or
        readByte().toLong().and(0xFF).shl(16) or
        readByte().toLong().and(0xFF).shl(24) or
        readByte().toLong().and(0xFF).shl(32) or
        readByte().toLong().and(0xFF).shl(40) or
        readByte().toLong().and(0xFF).shl(48) or
        readByte().toLong().and(0xFF).shl(56)

internal fun DataReader.readShort(): Short = readInt().toShort()

internal fun DataReader.readChar(): Char = readInt().toChar()

internal expect fun DataReader.readFloat(): Float

internal fun DataReader.readDouble(): Double = Double.fromBits(readLong())

internal fun DataReader.readBoolean(): Boolean = readByte() != 0.toByte()

@OptIn(ExperimentalStdlibApi::class)
internal fun DataReader.readString(): String? =
    readByteArray()
        ?.decodeToString()

internal fun DataReader.readByteArray(): ByteArray? =
    readSized { readByteArray(it) }

internal fun DataReader.readIntArray(): IntArray? =
    readSized {
        IntArray(it) { readInt() }
    }

internal fun DataReader.readLongArray(): LongArray? =
    readSized {
        LongArray(it) { readLong() }
    }

internal fun DataReader.readShortArray(): ShortArray? =
    readSized {
        ShortArray(it) { readShort() }
    }

internal fun DataReader.readFloatArray(): FloatArray? =
    readSized {
        FloatArray(it) { readFloat() }
    }

internal fun DataReader.readDoubleArray(): DoubleArray? =
    readSized {
        DoubleArray(it) { readDouble() }
    }

internal fun DataReader.readCharArray(): CharArray? =
    readSized {
        CharArray(it) { readChar() }
    }

internal fun DataReader.readBooleanArray(): BooleanArray? =
    readSized {
        BooleanArray(it) { readBoolean() }
    }

internal inline fun <T> DataReader.readList(readItem: DataReader.() -> T): List<T>? =
    readSized {
        List(it) { readItem() }
    }

internal inline fun <K, V> DataReader.readMap(readKey: DataReader.() -> K, readValue: DataReader.() -> V): Map<K, V>? =
    readSized {
        val map = mutableMapOf<K, V>()
        repeat(it) {
            map[readKey()] = readValue()
        }
        map
    }

internal inline fun <reified T : Enum<T>> DataReader.readEnum(): T =
    enumValues<T>()[readByte().toInt()]

private fun DataReader.readByteArray(size: Int): ByteArray =
    if (size == 0) {
        ByteArray(0)
    } else {
        ByteArray(size).also {
            read(it)
        }
    }

private inline fun <T> DataReader.readSized(read: DataReader.(Int) -> T): T? =
    readInt()
        .takeIf { it >= 0 }
        ?.let { read(it) }
