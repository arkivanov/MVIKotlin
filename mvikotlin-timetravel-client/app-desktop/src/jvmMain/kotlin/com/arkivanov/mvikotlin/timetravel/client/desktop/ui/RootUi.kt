package com.arkivanov.mvikotlin.timetravel.client.desktop.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.timetravel.client.desktop.subscribeAsState
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings

@Composable
fun RootUi(client: TimeTravelClient, settings: TimeTravelSettings) {
    Box(modifier = Modifier.fillMaxSize()) {
        val settingsModel by settings.models.subscribeAsState()

        TimeTravelClientUi(
            component = client,
            wrapEventDetails = settingsModel.settings.wrapEventDetails,
            onEditSettingsClicked = settings::onEditClicked,
        )

        TimeTravelSettingsUi(settings)
    }
}
