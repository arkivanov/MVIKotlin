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

    private const val ZIP_LEVEL = 9
    private val HEADER = "MVIKotlin".toByteArray()

    override fun serialize(export: TimeTravelExport): Result<ByteArray> =
        try {
            Result.Success(export.toByteArray())
        } catch (e: IOException) {
            Result.Error(e)
        }

    private fun TimeTravelExport.toByteArray(): ByteArray =
        ByteArrayOutputStream().use { output ->
            output.write(HEADER)

            ZipOutputStream(output).use { zipOutput ->
                zipOutput.setLevel(ZIP_LEVEL)
                zipOutput.putNextEntry(ZipEntry("export"))

                ObjectOutputStream(zipOutput).use { objectOutput ->
                    objectOutput.writeObject(this)
                    objectOutput.flush()
                }
            }

            output.toByteArray()
        }

    override fun deserialize(data: ByteArray): Result<TimeTravelExport> =
        try {
            Result.Success(data.toExport())
        } catch (e: Exception) {
            Result.Error(e)
        }

    private fun ByteArray.toExport(): TimeTravelExport =
        ByteArrayInputStream(this).use { input ->
            repeat(HEADER.size) { input.read() }

            ZipInputStream(input).use { zipInput ->
                zipInput.nextEntry

                ObjectInputStream(zipInput).use { objectInput ->
                    objectInput.readObject() as TimeTravelExport
                }
            }
        }
}
