package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT

@Suppress("FunctionName") // Factory function
fun TimeTravelClient(
    host: String = "localhost",
    port: Int = DEFAULT_PORT,
    view: TimeTravelClientView
): TimeTravelClient =
    TimeTravelClientImpl(
        connector = TimeTravelConnector(host = host, port = port),
        view = view
    )
