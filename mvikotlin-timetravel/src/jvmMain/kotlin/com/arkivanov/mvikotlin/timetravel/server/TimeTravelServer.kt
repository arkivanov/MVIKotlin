package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.export.DefaultTimeTravelExportSerializer
import com.arkivanov.mvikotlin.timetravel.export.TimeTravelExportSerializer
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT

class TimeTravelServer(
    runOnMainThread: (() -> Unit) -> Unit,
    controller: TimeTravelController = timeTravelController,
    port: Int = DEFAULT_PORT,
    exportSerializer: TimeTravelExportSerializer = DefaultTimeTravelExportSerializer,
    onError: (Throwable) -> Unit = {}
) {

    private val impl =
        TimeTravelServerImpl(
            runOnMainThread = runOnMainThread,
            controller = controller,
            port = port,
            exportSerializer = exportSerializer,
            onError = onError
        )

    fun start() {
        impl.start()
    }

    fun stop() {
        impl.stop()
    }
}
