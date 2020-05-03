package com.arkivanov.mvikotlin.plugin.idea.timetravel.client

import com.arkivanov.mvikotlin.plugin.idea.timetravel.runOnUiThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelStateUpdate
import org.jetbrains.annotations.CalledInAny
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.annotations.CalledInBackground
import java.io.IOException
import java.util.concurrent.BlockingQueue

internal class TimeTravelClient(
    private val host: String,
    private val port: Int,
    private val callbacks: Callbacks
) {

    private var state: State = State.Disconnected
    val isDisconnected: Boolean get() = state is State.Disconnected
    private val isConnected: Boolean get() = state is State.Connected

    @CalledInAwt
    fun connect() {
        if (!isDisconnected) {
            return
        }

        val connectionThread =
            ConnectionThread(
                host = host,
                port = port,
                onConnected = ::onConnected,
                onStateUpdateReceived = ::onStateUpdateReceived,
                onError = ::onError
            )

        state = State.Connecting(connectionThread)

        connectionThread.start()

        callbacks.onConnecting()
    }

    @CalledInAwt
    fun disconnect() {
        if (isDisconnected) {
            return
        }

        when (val state = state) {
            is State.Disconnected -> Unit
            is State.Connecting -> state.connectionThread.interrupt()
            is State.Connected -> state.connectionThread.interrupt()
        }.let {}

        state = State.Disconnected

        callbacks.onDisconnected()
    }

    @CalledInAwt
    fun send(command: TimeTravelCommand) {
        when (val state = state) {
            is State.Disconnected,
            is State.Connecting -> null
            is State.Connected -> state.commandQueue
        }
            ?.offer(command)
    }

    @CalledInBackground
    private fun onConnected(queue: BlockingQueue<TimeTravelCommand>) {
        runOnUiThreadIfNotDisconnected {
            state =
                when (val state = state) {
                    is State.Connecting -> State.Connected(state.connectionThread, queue)
                    is State.Disconnected,
                    is State.Connected -> state
                }

            if (isConnected) {
                callbacks.onConnected()
            }
        }
    }

    @CalledInBackground
    private fun onStateUpdateReceived(update: TimeTravelStateUpdate) {
        runOnUiThreadIfNotDisconnected {
            callbacks.onStateUpdateReceived(update)
        }
    }

    @CalledInBackground
    private fun onError(error: IOException) {
        runOnUiThreadIfNotDisconnected {
            callbacks.onError(error)
            disconnect()
        }
    }

    @CalledInAny
    private inline fun runOnUiThreadIfNotDisconnected(crossinline block: () -> Unit) {
        runOnUiThread {
            if (!isDisconnected) {
                block()
            }
        }
    }

    interface Callbacks {
        fun onConnecting()

        fun onConnected()

        fun onDisconnected()

        fun onError(error: IOException)

        fun onStateUpdateReceived(stateUpdate: TimeTravelStateUpdate)
    }

    private sealed class State {
        object Disconnected : State()
        class Connecting(val connectionThread: ConnectionThread) : State()
        class Connected(val connectionThread: ConnectionThread, val commandQueue: BlockingQueue<TimeTravelCommand>) : State()
    }
}
