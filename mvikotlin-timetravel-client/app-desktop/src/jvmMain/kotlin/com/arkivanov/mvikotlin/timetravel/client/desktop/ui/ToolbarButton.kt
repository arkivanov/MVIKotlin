package com.arkivanov.mvikotlin.timetravel.client.desktop.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ToolbarButton(toolTip: String, imageVector: ImageVector, enabled: Boolean = true, onClick: () -> Unit) {
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = MaterialTheme.colors.primaryVariant,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = toolTip,
                    modifier = Modifier.padding(10.dp),
                    color = Color.White
                )
            }
        },
        tooltipPlacement = TooltipPlacement.CursorPoint(alignment = Alignment.BottomEnd, offset = DpOffset((-24).dp, 16.dp))
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
            )
        }
    }
}
