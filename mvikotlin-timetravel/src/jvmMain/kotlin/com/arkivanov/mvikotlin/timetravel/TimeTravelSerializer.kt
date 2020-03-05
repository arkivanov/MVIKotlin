package com.arkivanov.mvikotlin.timetravel

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Base64
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class TimeTravelSerializer {

    fun serialize(events: List<TimeTravelEvent>): String =
        ByteArrayOutputStream().use { byteOutput ->
            ZipOutputStream(Base64.getEncoder().wrap(byteOutput)).use { zipOutput ->
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
        ZipInputStream(Base64.getDecoder().wrap(ByteArrayInputStream(data.toByteArray()))).use { zipInput ->
            zipInput.nextEntry

            @Suppress("UNCHECKED_CAST")
            ObjectInputStream(zipInput).use(ObjectInputStream::readObject) as List<TimeTravelEvent>
        }
}
