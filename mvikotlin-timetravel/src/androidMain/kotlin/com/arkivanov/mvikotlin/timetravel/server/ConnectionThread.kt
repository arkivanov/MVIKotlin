package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.closeSafe
import java.io.IOException
import java.net.ServerSocket

internal class ConnectionThread(
    private val port: Int,
    private val stateHolder: StateHolder,
    private val onCommandReceived: (TimeTravelCommand) -> Unit,
    private val onError: (IOException) -> Unit
) : Thread() {

    private val closeables = Closeables()

    override fun run() {
        val serverSocket =
            try {
                ServerSocket(port)
            } catch (e: IOException) {
                onError(e)
                return
            }

        closeables.add(serverSocket::closeSafe)

        try {
            while (!isInterrupted) {
                val socket =
                    try {
                        serverSocket.accept()
                    } catch (e: IOException) {
                        onError(e)
                        return
                    }

                val reader = ReaderThread(socket, onCommandReceived)
                reader.start()
                val stateSupplier = StateSupplier(stateHolder)
                val writer = WriterThread(socket, stateSupplier::take)
                writer.start()

                closeables.add {
                    socket.closeSafe()
                    reader.interrupt()
                    writer.interrupt()
                }
            }
        } catch (e: IOException) {
            onError(e)
        } catch (ignored: InterruptedException) {
            interrupt()
        } finally {
            closeables.close()
        }
    }

    override fun interrupt() {
        closeables.close()

        super.interrupt()
    }

    private class StateSupplier(
        private val stateHolder: StateHolder
    ) {
        private var previousState: TimeTravelState? = null

        @Throws(InterruptedException::class)
        fun take(): TimeTravelStateUpdate {
            val previousState = previousState
            val state = stateHolder.getNew(previousState)

            val update =
                TimeTravelStateUpdate(
                    eventsUpdate = diffEvents(new = state.events, previous = previousState?.events),
                    selectedEventIndex = state.selectedEventIndex,
                    mode = state.mode.toProto()
                )

            this.previousState = state

            return update
        }

        private fun diffEvents(new: List<TimeTravelEvent>, previous: List<TimeTravelEvent>?): TimeTravelEventsUpdate =
            when {
                previous == null -> TimeTravelEventsUpdate.All(new.toProto())
                new.size > previous.size -> TimeTravelEventsUpdate.New(new.subList(previous.size, new.size).toProto())
                new.size == previous.size -> TimeTravelEventsUpdate.New(emptyList())
                else -> TimeTravelEventsUpdate.All(new.toProto())
            }
    }
}
