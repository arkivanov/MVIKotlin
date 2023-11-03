package com.arkivanov.mvikotlin.timetravel.client.desktop.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.timetravel.client.desktop.subscribeAsState
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings.Model

@Composable
fun TimeTravelSettingsUi(component: TimeTravelSettings) {
    val model by component.models.subscribeAsState()

    model.editing?.also { editing ->
        SettingsDialog(
            editing = editing,
            events = SettingsEvents(
                onCancel = component::onCancelClicked,
                onSave = component::onSaveClicked,
                onHostChanged = component::onHostChanged,
                onPortChanged = component::onPortChanged,
                onConnectViaAdbChanged = component::onConnectViaAdbChanged,
                onWrapEventDetailsChanged = component::onWrapEventDetailsChanged,
                onDarkModeChanged = component::onDarkModeChanged
            )
        )
    }
}

@Composable
private fun SettingsDialog(
    editing: Model.Editing,
    events: SettingsEvents
) {
    AlertDialog(
        onDismissRequest = events.onCancel,
        confirmButton = {
            Button(onClick = events.onSave) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            Button(onClick = events.onCancel) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Settings") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = editing.host,
                    onValueChange = events.onHostChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Host") }
                )

                TextField(
                    value = editing.port,
                    onValueChange = events.onPortChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Port") }
                )

                CheckboxSetting(
                    text = "Connect via ADB",
                    isChecked = editing.connectViaAdb,
                    onChanged = events.onConnectViaAdbChanged
                )

                CheckboxSetting(
                    text = "Wrap event details",
                    isChecked = editing.wrapEventDetails,
                    onChanged = events.onWrapEventDetailsChanged
                )

                editing.isDarkMode?.also { isDarkMode ->
                    CheckboxSetting(
                        text = "Dark mode",
                        isChecked = isDarkMode,
                        onChanged = events.onDarkModeChanged
                    )
                }
            }
        },
    )
}

@Composable
private fun CheckboxSetting(text: String, isChecked: Boolean, onChanged: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onChanged(!isChecked) }
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onChanged
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = text)
    }
}

private class SettingsEvents(
    val onSave: () -> Unit,
    val onCancel: () -> Unit,
    val onHostChanged: (String) -> Unit,
    val onPortChanged: (String) -> Unit,
    val onConnectViaAdbChanged: (Boolean) -> Unit,
    val onWrapEventDetailsChanged: (Boolean) -> Unit,
    val onDarkModeChanged: (Boolean) -> Unit
)
