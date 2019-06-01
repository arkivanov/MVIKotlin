package com.arkivanov.mvidroid.store.timetravel

import android.support.annotation.WorkerThread
import android.util.Base64
import android.util.Base64InputStream
import android.util.Base64OutputStream
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class MviTimeTravelSerializer {

    fun serialize(events: MviTimeTravelEvents): Single<String> =
        Single
            .just(events)
            .observeOn(Schedulers.computation())
            .map { it.serialize() }
            .observeOn(AndroidSchedulers.mainThread())

    fun deserialize(data: String): Single<MviTimeTravelEvents> =
        Single
            .just(data)
            .observeOn(Schedulers.computation())
            .map { it.deserialize() as MviTimeTravelEvents }
            .observeOn(AndroidSchedulers.mainThread())

    private companion object {
        @WorkerThread
        private fun Any.serialize(): String =
            ByteArrayOutputStream().use { byteOutput ->
                ZipOutputStream(Base64OutputStream(byteOutput, Base64.DEFAULT)).use { zipOutput ->
                    zipOutput.setLevel(9)
                    zipOutput.putNextEntry(ZipEntry("events"))

                    ObjectOutputStream(zipOutput).use { output ->
                        output.writeObject(this)
                        output.flush()
                    }
                }

                byteOutput.toString()
            }

        @WorkerThread
        private fun String.deserialize(): Any? =
            ZipInputStream(Base64InputStream(ByteArrayInputStream(toByteArray()), Base64.DEFAULT)).use { zipInput ->
                zipInput.nextEntry

                ObjectInputStream(zipInput)
                    .use(ObjectInputStream::readObject)
            }
    }
}