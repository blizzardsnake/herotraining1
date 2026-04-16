package com.herotraining.ui.theme

import androidx.compose.ui.graphics.Color

/** Matches the prototype palette exactly. */
object HeroPalette {
    val Black = Color(0xFF000000)
    val AlmostBlack = Color(0xFF0A0A0A)
    val NearBlack = Color(0xFF0F0F0F)
    val Red500 = Color(0xFFDC2626)
    val Red600 = Color(0xFFC9302C)
    val Red900 = Color(0x80450000)
    val Purple600 = Color(0xFFA855F7)
    val Purple800 = Color(0xFF8B5CF6)
    val Gold = Color(0xFFFFD700)
    val Yellow = Color(0xFFE63946)

    // neutral grays (matching Tailwind)
    val Neutral600 = Color(0xFF525252)
    val Neutral500 = Color(0xFF737373)
    val Neutral400 = Color(0xFFA3A3A3)
    val Neutral300 = Color(0xFFD4D4D4)
    val Neutral700 = Color(0xFF404040)
    val Neutral800 = Color(0xFF262626)
    val Neutral900 = Color(0xFF171717)
    val Neutral950 = Color(0xFF0A0A0A)
}

/** Per-hero colors used in UI. */
data class HeroColors(
    val accent: Color,
    val background: Color,
)

fun Color.withAlphaFraction(fraction: Float): Color =
    copy(alpha = fraction.coerceIn(0f, 1f))
