package com.arkivanov.mvikotlin.timetravel.proto.internal.io

internal class DataWriter {

    var data: ByteArray = ByteArray(size = 1024)
        private set

    var size: Int = 0
        private set

    fun reset() {
        size = 0
    }

    fun writeByte(value: Byte) {
        ensureFreeSpace(1)
        data[size++] = value
    }

    fun write(array: ByteArray, startIndex: Int = 0, endIndex: Int = array.size) {
        val dataSize = endIndex - startIndex
        ensureFreeSpace(dataSize)
        array.copyInto(destination = data, destinationOffset = size, startIndex = startIndex, endIndex = endIndex)
        size += dataSize
    }

    private fun ensureFreeSpace(size: Int) {
        val requiredSize = this.size + size
        if (data.size < requiredSize) {
            data = data.copyOf(requiredSize.getNextPowerOfTwo())
        }
    }

    private fun Int.getNextPowerOfTwo(): Int =
        takeHighestOneBit()
            .takeUnless { it == this }
            ?.shl(1)
            ?: this
}
