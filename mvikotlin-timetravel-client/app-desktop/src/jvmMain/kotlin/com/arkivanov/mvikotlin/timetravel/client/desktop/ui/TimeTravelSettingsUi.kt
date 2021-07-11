package com.arkivanov.mvikotlin.timetravel.client.desktop.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    PopupDialog(title = "Settings", onDismissRequest = events.onCancel) {
        Column(modifier = Modifier.padding(16.dp).width(IntrinsicSize.Min)) {
            TextField(
                value = editing.host,
                onValueChange = events.onHostChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Host") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = editing.port,
                onValueChange = events.onPortChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Port") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            CheckboxSetting(
                text = "Connect via ADB",
                isChecked = editing.connectViaAdb,
                onChanged = events.onConnectViaAdbChanged
            )

            Spacer(modifier = Modifier.height(8.dp))

            CheckboxSetting(
                text = "Wrap event details",
                isChecked = editing.wrapEventDetails,
                onChanged = events.onWrapEventDetailsChanged
            )

            Spacer(modifier = Modifier.height(8.dp))

            editing.isDarkMode?.also { isDarkMode ->
                CheckboxSetting(
                    text = "Dark mode",
                    isChecked = isDarkMode,
                    onChanged = events.onDarkModeChanged
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(modifier = Modifier.align(Alignment.End)) {
                Button(onClick = events.onCancel) {
                    Text(text = "Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = events.onSave) {
                    Text(text = "Save")
                }
            }
        }
    }
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
