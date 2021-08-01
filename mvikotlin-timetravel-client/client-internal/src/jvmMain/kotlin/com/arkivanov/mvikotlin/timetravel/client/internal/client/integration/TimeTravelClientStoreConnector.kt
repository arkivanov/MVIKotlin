package com.arkivanov.mvikotlin.timetravel.client.internal.client.integration

import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStoreFactory.Connector
import com.arkivanov.mvikotlin.timetravel.client.internal.closeSafe
import com.arkivanov.mvikotlin.timetravel.client.internal.closeSafeAsync
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue.TimeTravelEventValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelexport.TimeTravelExport
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableEmitter
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.onErrorReturn
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import java.net.Socket

internal actual class TimeTravelClientStoreConnector actual constructor(
    private val host: () -> String,
    private val port: () -> Int
) : Connector {

    override fun connect(): Observable<Connector.Event> =
        observable<Connector.Event> { it.connect() }
            .onErrorReturn { Connector.Event.Error(it.message) }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)

    private fun ObservableEmitter<Connector.Event>.connect() {
        val socket = Socket(host(), port())

        if (isDisposed) {
            socket.closeSafe()
            return
        }

        val reader =
            ReaderThread<ProtoObject>(
                socket = socket,
                onRead = {
                    onNext(
                        when (it) {
                            is TimeTravelStateUpdate -> Connector.Event.StateUpdate(it)
                            is TimeTravelEventValue -> Connector.Event.EventValue(eventId = it.eventId, value = it.value)
                            is TimeTravelExport -> Connector.Event.ExportEvents(it.data)
                            else -> {
                                onError(UnsupportedOperationException("Unsupported proto object type: $it"))
                                return@ReaderThread
                            }
                        }
                    )
                },
                onError = ::onError
            )

        val writer = WriterThread(socket = socket, onError = ::onError)

        onNext(Connector.Event.Connected(writer::write))

        reader.start()
        writer.start()

        setDisposable(
            Disposable {
                reader.interrupt()
                writer.interrupt()
                socket.closeSafeAsync()
            }
        )
    }
}
