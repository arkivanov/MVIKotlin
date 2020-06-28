package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelexport

import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readByteArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeByteArray

internal fun DataWriter.writeTimeTravelExport(timeTravelExport: TimeTravelExport) {
    writeByteArray(timeTravelExport.data)
}

internal fun DataReader.readTimeTravelExport(): TimeTravelExport =
    TimeTravelExport(
        data = readByteArray()!!
    )
