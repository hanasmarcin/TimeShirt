package com.example.timeshirt.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    background = Color.Black,
    primaryContainer = Color.Black
)

private val LightColorPalette = lightColorScheme()

@Composable
fun TimeShirtTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorPalette
    else LightColorPalette

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}