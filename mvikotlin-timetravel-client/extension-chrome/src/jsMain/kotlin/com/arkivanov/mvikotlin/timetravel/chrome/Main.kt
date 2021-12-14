package com.arkivanov.mvikotlin.timetravel.chrome

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelClientComponent
import org.jetbrains.compose.web.renderComposableInBody

fun main() {
    val client = client()

    renderComposableInBody {
        TimeTravelClientContent(client)
    }
}

private fun client(): TimeTravelClient {
    val lifecycle = LifecycleRegistry()

    val clientComponent =
        TimeTravelClientComponent(
            lifecycle = lifecycle,
            storeFactory = DefaultStoreFactory(),
            connector = ChromeConnector(),
            onImportEvents = { null },
            onExportEvents = {},
        )

    lifecycle.resume()

    return clientComponent
}
