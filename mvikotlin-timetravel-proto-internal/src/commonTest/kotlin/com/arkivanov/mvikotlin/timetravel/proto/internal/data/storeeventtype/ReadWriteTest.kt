package com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<StoreEventType>() {

    @Test
    fun writes_and_reads_StoreEventType() {
        StoreEventType.values().forEach {
            testWriteRead(it)
        }
    }

    override fun DataWriter.writeObject(obj: StoreEventType) {
        writeStoreEventType(obj)
    }

    override fun DataReader.readObject(): StoreEventType = readStoreEventType()
}
