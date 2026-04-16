package com.herotraining.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * In the React prototype all headings use Impact sans-serif. On Android we approximate it with
 * the system condensed sans-serif (close to Oswald/Bebas). Body text uses default.
 */
val ImpactLike = FontFamily.SansSerif

private val baseTypography = Typography()

val HeroTypography = baseTypography.copy(
    displayLarge = baseTypography.displayLarge.copy(fontFamily = ImpactLike, fontWeight = FontWeight.Black),
    displayMedium = baseTypography.displayMedium.copy(fontFamily = ImpactLike, fontWeight = FontWeight.Black),
    displaySmall = baseTypography.displaySmall.copy(fontFamily = ImpactLike, fontWeight = FontWeight.Black),
    headlineLarge = baseTypography.headlineLarge.copy(fontFamily = ImpactLike, fontWeight = FontWeight.Black),
    headlineMedium = baseTypography.headlineMedium.copy(fontFamily = ImpactLike, fontWeight = FontWeight.Black),
    headlineSmall = baseTypography.headlineSmall.copy(fontFamily = ImpactLike, fontWeight = FontWeight.Black),
    titleLarge = baseTypography.titleLarge.copy(fontFamily = ImpactLike, fontWeight = FontWeight.Black)
)

val HeroTagStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 11.sp,
    letterSpacing = 3.sp
)
