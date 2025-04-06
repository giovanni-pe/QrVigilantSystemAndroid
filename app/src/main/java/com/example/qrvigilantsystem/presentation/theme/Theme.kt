package com.example.qrvigilantsystem.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MilitaryGreen = Color(0xFF4CAF50)
val MilitaryDarkGreen = Color(0xFF2E7D32)
val MilitaryYellow = Color(0xFFFFC107)
val MilitaryRed = Color(0xFFD32F2F)
val MilitaryDarkGray = Color(0xFF212121)
val MilitaryLightGray = Color(0xFF757575)

private val DarkColorScheme = darkColorScheme(
    primary = MilitaryGreen,
    secondary = MilitaryDarkGreen,
    background = Color.Black,
    surface = MilitaryDarkGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = MilitaryYellow,
    onSurface = MilitaryLightGray
)

@Composable
fun MilitaryScannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}