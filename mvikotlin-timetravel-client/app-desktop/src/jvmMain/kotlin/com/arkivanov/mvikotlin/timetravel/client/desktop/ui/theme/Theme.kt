package com.arkivanov.mvikotlin.timetravel.client.desktop.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightGreenColorPalette =
    lightColors(
        primary = indigo500,
        primaryVariant = indigo700,
        secondary = green200,
        secondaryVariant = green400,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
        error = Color.Red,
    )

private val DarkGreenColorPalette =
    darkColors(
        primary = indigo200,
        primaryVariant = indigo700,
        secondary = green200,
        secondaryVariant = green400,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
        error = Color.Red,
    )

@Composable
fun TimeTravelClientTheme(
    isDarkMode: Boolean = false,
    colorsOverride: (Colors) -> Colors = { it },
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = colorsOverride(if (isDarkMode) DarkGreenColorPalette else LightGreenColorPalette),
        content = content
    )
}
