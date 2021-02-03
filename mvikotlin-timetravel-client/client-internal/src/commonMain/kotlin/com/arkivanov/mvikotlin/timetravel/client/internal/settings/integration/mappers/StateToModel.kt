package com.arkivanov.mvikotlin.timetravel.client.internal.settings.integration.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings.Model
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.store.TimeTravelSettingsStore.State

internal val stateToModel: State.() -> Model =
    {
        Model(
            settings = settings.toSettingsModel(),
            editing = editing?.toEditingModel()
        )
    }

private fun State.Settings.toSettingsModel(): Model.Settings =
    Model.Settings(
        host = host,
        port = port,
        connectViaAdb = connectViaAdb,
        wrapEventDetails = wrapEventDetails,
        isDarkMode = isDarkMode
    )

private fun State.Editing.toEditingModel(): Model.Editing =
    Model.Editing(
        host = host,
        port = port,
        connectViaAdb = connectViaAdb,
        wrapEventDetails = wrapEventDetails,
        isDarkMode = isDarkMode
    )
