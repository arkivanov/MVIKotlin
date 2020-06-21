package com.arkivanov.mvikotlin.timetravel.proto.internal.io

internal class DataReader(
    private val data: ByteArray
) {

    private var index = 0

    fun readByte(): Byte = data[index++]

    fun read(array: ByteArray, startIndex: Int = 0, endIndex: Int = array.size) {
        data.copyInto(destination = array, destinationOffset = startIndex, startIndex = index, endIndex = index + endIndex - startIndex)
        index += endIndex - startIndex
    }
}
