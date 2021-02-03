package com.arkivanov.mvikotlin.timetravel.client.desktop.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
internal fun PopupDialog(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Popup(
        alignment = Alignment.Center,
        focusable = true,
        onDismissRequest = onDismissRequest
    ) {
        Card(elevation = 8.dp) {
            Column(modifier = Modifier.width(IntrinsicSize.Min)) {
                Text(
                    text = title,
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.SemiBold
                )

                Divider()

                content()
            }
        }
    }
}

