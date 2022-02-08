package com.arkivanov.mvikotlin.timetravel.client.internal.client

import com.arkivanov.mvikotlin.timetravel.client.internal.client.Connector.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.closeSafe
import com.arkivanov.mvikotlin.timetravel.client.internal.closeSafeAsync
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue.TimeTravelEventValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelexport.TimeTravelExport
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.map
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableEmitter
import com.badoo.reaktive.observable.concat
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.onErrorReturn
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.observable.takeUntil
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.notNull
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn
import java.net.Socket

class DefaultConnector(
    private val forwardAdbPort: () -> Error?,
    private val host: () -> String,
    private val port: () -> Int,
) : Connector {

    override fun connect(): Observable<Event> =
        concat(forwardPort(), connectSocket())
            .takeUntil { it is Event.Error }
            .onErrorReturn { Event.Error(it.message) }

    private fun forwardPort(): Observable<Event> =
        singleFromFunction(forwardAdbPort)
            .subscribeOn(mainScheduler)
            .notNull()
            .map { Event.Error(text = it.text) }
            .asObservable()

    private fun connectSocket(): Observable<Event> =
        observable<Event> { it.connectSocket() }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)

    private fun ObservableEmitter<Event>.connectSocket() {
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
                            is TimeTravelStateUpdate -> Event.StateUpdate(it)
                            is TimeTravelEventValue -> Event.EventValue(eventId = it.eventId, value = it.value)
                            is TimeTravelExport -> Event.ExportEvents(it.data)
                            else -> Event.Error(text = "Unsupported proto object type: $it")
                        }
                    )
                },
                onError = ::onError
            )

        val writer = WriterThread(socket = socket, onError = ::onError)

        onNext(Event.Connected(writer::write))

        reader.start()
        writer.start()

        setCancellable {
            reader.interrupt()
            writer.interrupt()
            socket.closeSafeAsync()
        }
    }

    data class Error(val text: String)
}
