package com.arkivanov.mvikotlin.timetravel.proto.internal.io

internal actual fun DataWriter.writeFloat(value: Float) {
    writeInt(value.toRawBits())
}
