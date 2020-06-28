package com.arkivanov.mvikotlin.timetravel.export

import com.arkivanov.mvikotlin.timetravel.export.TimeTravelExportSerializer.Result
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object DefaultTimeTravelExportSerializer : TimeTravelExportSerializer {

    private val HEADER = "MVIKotlin".toByteArray()

    override fun serialize(export: TimeTravelExport): Result<ByteArray> =
        try {
            ByteArrayOutputStream().use { output ->
                output.write(HEADER)

                ZipOutputStream(output).use { zipOutput ->
                    zipOutput.setLevel(9)
                    zipOutput.putNextEntry(ZipEntry("export"))

                    ObjectOutputStream(zipOutput).use { objectOutput ->
                        objectOutput.writeObject(export)
                        objectOutput.flush()
                    }
                }

                Result.Success(output.toByteArray())
            }
        } catch (e: IOException) {
            Result.Error(e)
        }

    override fun deserialize(data: ByteArray): Result<TimeTravelExport> =
        try {
            ByteArrayInputStream(data).use { input ->
                repeat(HEADER.size) { input.read() }

                ZipInputStream(input).use { zipInput ->
                    zipInput.nextEntry

                    ObjectInputStream(zipInput).use { objectInput ->
                        Result.Success(objectInput.readObject() as TimeTravelExport)
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
}
