package com.arkivanov.mvikotlin.timetravel

import android.util.Base64
import android.util.Base64InputStream
import android.util.Base64OutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class TimeTravelSerializer {

    fun serialize(events: List<TimeTravelEvent>): String =
        ByteArrayOutputStream().use { byteOutput ->
            ZipOutputStream(Base64OutputStream(byteOutput, Base64.DEFAULT)).use { zipOutput ->
                zipOutput.setLevel(9)
                zipOutput.putNextEntry(ZipEntry("events"))

                ObjectOutputStream(zipOutput).use { output ->
                    output.writeObject(events)
                    output.flush()
                }
            }

            byteOutput.toString()
        }

    fun deserialize(data: String): List<TimeTravelEvent> =
        ZipInputStream(Base64InputStream(ByteArrayInputStream(data.toByteArray()), Base64.DEFAULT)).use { zipInput ->
            zipInput.nextEntry

            @Suppress("UNCHECKED_CAST")
            ObjectInputStream(zipInput).use(ObjectInputStream::readObject) as List<TimeTravelEvent>
        }
}
