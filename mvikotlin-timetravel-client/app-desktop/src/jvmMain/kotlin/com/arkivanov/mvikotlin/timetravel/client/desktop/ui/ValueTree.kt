package com.arkivanov.mvikotlin.timetravel.client.desktop.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ValueTree(
    node: ValueNode,
    title: @Composable (text: String) -> Unit,
    modifier: Modifier = Modifier,
    isInitiallyExpanded: Boolean = false
) {
    if (node.children.isEmpty()) {
        Box(modifier = modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp)) {
            title(node.title)
        }
    } else {
        Column(modifier = modifier) {
            var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                ExpandButton(
                    isExpanded = isExpanded,
                    onClick = { isExpanded = !isExpanded }
                )

                Box(modifier = Modifier.clickable { isExpanded = !isExpanded }.padding(top = 2.dp, bottom = 2.dp)) {
                    title(node.title)
                }
            }

            if (isExpanded) {
                node.children.forEach {
                    ValueTree(
                        node = it,
                        title = title,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandButton(
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
        contentDescription = if (isExpanded) "Collapse" else "Expand",
        modifier = Modifier
            .clickable(
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember(::MutableInteractionSource),
                indication = rememberRipple(bounded = false, radius = 12.dp)
            )
            .size(16.dp)
    )
}
