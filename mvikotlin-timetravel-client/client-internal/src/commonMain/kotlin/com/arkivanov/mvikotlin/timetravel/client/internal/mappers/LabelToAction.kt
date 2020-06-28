package com.arkivanov.mvikotlin.timetravel.client.internal.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientStore
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Action

internal val labelToAction: TimeTravelClientStore.Label.() -> Action? =
    {
        when (this) {
            is TimeTravelClientStore.Label.ExportEvents -> Action.ExportEvents(data = data)
            is TimeTravelClientStore.Label.Error -> Action.ShowError(text = text ?: "Unknown error")
        }
    }
