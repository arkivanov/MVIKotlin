package com.arkivanov.mvikotlin.timetravel.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelCommand
import java.io.IOException

class TimeTravelServer(
    private val controller: TimeTravelController = timeTravelController,
    private val port: Int = DEFAULT_PORT,
    private val onError: (IOException) -> Unit = {}
) {

    private val handler = Handler(Looper.getMainLooper())
    private var holder: Holder? = null

    fun start() {
        if (holder != null) {
            return
        }

        val stateHolder = StateHolder(controller.state)

        val connectionThread =
            ConnectionThread(
                port = port,
                stateHolder = stateHolder,
                onCommandReceived = ::onCommandReceived,
                onError = ::onError
            )

        val disposable = controller.states(observer(onNext = stateHolder::offer))
        holder = Holder(connectionThread, disposable)
        connectionThread.start()
    }

    fun stop() {
        val holder = holder
        this.holder = null
        holder?.connectionThread?.interrupt()
        holder?.disposable?.dispose()
    }

    private fun onCommandReceived(command: TimeTravelCommand) {
        runOnMainThreadIfNotDisposed {
            when (command) {
                is TimeTravelCommand.StartRecording -> controller.startRecording()
                is TimeTravelCommand.StopRecording -> controller.stopRecording()
                is TimeTravelCommand.MoveToStart -> controller.moveToStart()
                is TimeTravelCommand.StepBackward -> controller.stepBackward()
                is TimeTravelCommand.StepForward -> controller.stepForward()
                is TimeTravelCommand.MoveToEnd -> controller.moveToEnd()
                is TimeTravelCommand.Cancel -> controller.cancel()
                is TimeTravelCommand.DebugEvent -> controller.debugEvent(eventId = command.eventId)
            }.let {}
        }
    }

    private fun onError(error: IOException) {
        runOnMainThreadIfNotDisposed {
            Log.e("MviKotlin", "TimeTravelServer error: $error")
            onError.invoke(error)
        }
    }

    private inline fun runOnMainThreadIfNotDisposed(crossinline block: () -> Unit) {
        handler.post {
            if (holder != null) {
                block()
            }
        }
    }

    private class Holder(
        val connectionThread: ConnectionThread,
        val disposable: Disposable
    )
}
