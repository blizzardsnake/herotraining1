package com.herotraining.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.herotraining.R

/** Rajdhani — sci-fi condensed sans. Used for headlines (was "ImpactLike"). */
val Rajdhani = FontFamily(
    Font(R.font.rajdhani_regular, FontWeight.Normal),
    Font(R.font.rajdhani_medium, FontWeight.Medium),
    Font(R.font.rajdhani_bold, FontWeight.Bold)
)

/**
 * "Orbitron slot" — monospace-style status labels & small caps HUD text.
 * Originally planned real Orbitron TTF, but Google's CDN kept returning 404-HTML instead of the
 * binary, which crashed Compose with `IllegalStateException: Could not load font` on first text
 * render. System Monospace is always available, renders cleanly, and matches the HUD aesthetic.
 * Safe permanent replacement — costs zero bytes of APK weight.
 */
val Orbitron: FontFamily = FontFamily.Monospace

/** Legacy alias so existing code keeps compiling — pointed to Rajdhani now. */
val ImpactLike: FontFamily = Rajdhani

private val baseTypography = Typography()

val HeroTypography = baseTypography.copy(
    displayLarge = baseTypography.displayLarge.copy(fontFamily = Rajdhani, fontWeight = FontWeight.Bold),
    displayMedium = baseTypography.displayMedium.copy(fontFamily = Rajdhani, fontWeight = FontWeight.Bold),
    displaySmall = baseTypography.displaySmall.copy(fontFamily = Rajdhani, fontWeight = FontWeight.Bold),
    headlineLarge = baseTypography.headlineLarge.copy(fontFamily = Rajdhani, fontWeight = FontWeight.Bold),
    headlineMedium = baseTypography.headlineMedium.copy(fontFamily = Rajdhani, fontWeight = FontWeight.Bold),
    headlineSmall = baseTypography.headlineSmall.copy(fontFamily = Rajdhani, fontWeight = FontWeight.Bold),
    titleLarge = baseTypography.titleLarge.copy(fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
)

val HeroTagStyle = TextStyle(
    fontFamily = Orbitron,
    fontSize = 11.sp,
    letterSpacing = 3.sp
)
