package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT

@Suppress("FunctionName") // Factory function
fun TimeTravelClient(view: TimeTravelClientView): TimeTravelClient =
    TimeTravelClient(
        host = "localhost",
        port = DEFAULT_PORT,
        view = view
    )
