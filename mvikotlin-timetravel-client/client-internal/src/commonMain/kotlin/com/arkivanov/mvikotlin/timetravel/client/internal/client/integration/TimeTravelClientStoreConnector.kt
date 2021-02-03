package com.arkivanov.mvikotlin.timetravel.client.internal.client.integration

import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStoreFactory

internal expect class TimeTravelClientStoreConnector(
    host: () -> String,
    port: () -> Int
) : TimeTravelClientStoreFactory.Connector
