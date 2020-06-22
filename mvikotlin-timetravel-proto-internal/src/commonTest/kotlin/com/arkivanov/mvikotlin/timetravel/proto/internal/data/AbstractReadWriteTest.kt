package com.arkivanov.mvikotlin.timetravel.proto.internal.data

import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.assertEquals

internal abstract class AbstractReadWriteTest<T : Any> {

    protected fun testWriteRead(obj: T) {
        val writer = DataWriter()
        writer.writeObject(obj)
        val reader = DataReader(writer.data)
        val readObj = reader.readObject()

        assertEquals(obj, readObj)
    }

    protected abstract fun DataWriter.writeObject(obj: T)

    protected abstract fun DataReader.readObject(): T
}
