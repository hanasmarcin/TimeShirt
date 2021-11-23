package com.example.timeshirt.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import com.example.timeshirt.R

// Set of Material typography styles to start with
val Fonts = FontFamily(
    Font(R.font.atkinson_hyperlegible_bold, weight = FontWeight.Bold),
    Font(
        R.font.atkinson_hyperlegible_bold_italic,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
    Font(R.font.atkinson_hyperlegible_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.atkinson_hyperlegible_regular, weight = FontWeight.Normal)
)

val Typography = androidx.compose.material3.Typography(
    headlineLarge = TextStyle(fontFamily = Fonts),
    headlineMedium = TextStyle(fontFamily = Fonts),
    headlineSmall = TextStyle(fontFamily = Fonts),
    titleLarge = TextStyle(fontFamily = Fonts),
    titleMedium = TextStyle(fontFamily = Fonts),
    titleSmall = TextStyle(fontFamily = Fonts),
    bodyLarge = TextStyle(fontFamily = Fonts),
    bodyMedium = TextStyle(fontFamily = Fonts),
    bodySmall = TextStyle(fontFamily = Fonts),
    labelLarge = TextStyle(fontFamily = Fonts),
    labelMedium = TextStyle(fontFamily = Fonts),
    labelSmall = TextStyle(fontFamily = Fonts),
)
