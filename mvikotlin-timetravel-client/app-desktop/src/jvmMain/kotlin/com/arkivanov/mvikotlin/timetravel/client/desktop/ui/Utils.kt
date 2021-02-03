package com.arkivanov.mvikotlin.timetravel.client.desktop.ui

import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun contentColor(alpha: Float = contentAlpha()): Color = LocalContentColor.current.copy(alpha = alpha)

@Composable
internal fun contentAlpha(): Float = LocalContentAlpha.current
