package com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype

import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeEnum

internal fun DataWriter.writeStoreEventType(storeEventType: StoreEventType) {
    writeEnum(storeEventType)
}

internal fun DataReader.readStoreEventType(): StoreEventType = readEnum()
