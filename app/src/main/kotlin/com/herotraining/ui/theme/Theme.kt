package com.herotraining.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HeroDarkColors = darkColorScheme(
    primary = Color(0xFFDC2626),
    onPrimary = Color.White,
    secondary = Color(0xFFFFD700),
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF0A0A0A),
    onSurface = Color.White,
    error = Color(0xFFDC2626)
)

@Composable
fun HeroTrainingTheme(
    darkTheme: Boolean = true, // always dark
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HeroDarkColors,
        typography = HeroTypography,
        content = content
    )
}
