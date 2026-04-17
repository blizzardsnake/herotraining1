package com.herotraining.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.herotraining.R

/**
 * Per-hero theme: background drawable + readable color palette + accent tuned for the bg.
 *
 * The `heroColor` stays as the hero's signature accent, but `onBgPrimary` / `onBgSecondary`
 * are picked to contrast with the specific background image so text stays readable.
 */
data class HeroTheme(
    val heroId: String,
    val backgroundRes: Int?,         // null for generic / unbranded screens
    val heroColor: Color,            // accent (matches hero.color in catalog)
    val heroBgColor: Color,          // fallback solid bg for small panels
    val onBgPrimary: Color,          // primary text color readable on bg
    val onBgSecondary: Color,        // secondary/muted text color
    val cardOverlay: Color,          // tint applied beneath cards for contrast (semi-transparent black)
    val glowColor: Color             // for future glow / highlight effects
)

/** Fallback theme when no hero is active (boot, sign-in, anketa). */
val DefaultHeroTheme = HeroTheme(
    heroId = "",
    backgroundRes = null,
    heroColor = HeroPalette.Red500,
    heroBgColor = HeroPalette.Black,
    onBgPrimary = Color.White,
    onBgSecondary = HeroPalette.Neutral400,
    cardOverlay = Color.Black.copy(alpha = 0.5f),
    glowColor = HeroPalette.Red500
)

/**
 * Catalog: map hero id -> HeroTheme.
 * Palette choices explained:
 * — Leon: bg is mostly cold grey + red emergency light → text almost-white, accent keep brand steel-blue
 * — Dante: warm red interior with amber jukebox → text cream-white, accent signature red
 * — Kratos: dark mountain + aurora pink/green → text silvery, accent blood red
 * — Sung Jin-Woo: violet dungeon → text white, accent vivid violet
 * — Ada: dark grey rooftop + crimson lanterns → text cream, accent crimson
 * — Lara: dark green jungle + amber torch → text cream, accent emerald
 * — 2B: monochrome grey ruins → text white, accent cool steel
 * — Ciri: snow + elder-blood green wisps → text silver, accent pale green
 */
object HeroThemes {

    val LEON = HeroTheme(
        heroId = "leon",
        backgroundRes = R.drawable.bg_leon,
        heroColor = Color(0xFF5A8FCC),         // lifted to a brighter steel-blue for legibility on the dark bg
        heroBgColor = Color(0xFF0A0F1A),
        onBgPrimary = Color(0xFFF0F4F8),
        onBgSecondary = Color(0xFF9AA8B8),
        cardOverlay = Color(0xCC0A0F1A),
        glowColor = Color(0xFFE63946)          // red emergency glow accent
    )

    val DANTE = HeroTheme(
        heroId = "dante",
        backgroundRes = R.drawable.bg_dante,
        heroColor = Color(0xFFFF4D5E),
        heroBgColor = Color(0xFF1A0505),
        onBgPrimary = Color(0xFFFFE8D6),       // cream-white for warm bg
        onBgSecondary = Color(0xFFBF8B7F),
        cardOverlay = Color(0xCC140505),
        glowColor = Color(0xFFFF7A1A)          // amber jukebox glow
    )

    val KRATOS = HeroTheme(
        heroId = "kratos",
        backgroundRes = R.drawable.bg_kratos,
        heroColor = Color(0xFFE74C3C),         // blood red, not too dark so it pops on grey mountain
        heroBgColor = Color(0xFF0F0605),
        onBgPrimary = Color(0xFFE8E3DC),
        onBgSecondary = Color(0xFFA39A8F),
        cardOverlay = Color(0xCC0F0605),
        glowColor = Color(0xFFB16CEF)          // aurora pink-violet highlight
    )

    val SUNG_JINWOO = HeroTheme(
        heroId = "sung_jinwoo",
        backgroundRes = R.drawable.bg_jinwoo,
        heroColor = Color(0xFFB388FF),         // brighter violet (brand #8B5CF6 is too dim on the purple bg)
        heroBgColor = Color(0xFF0A0514),
        onBgPrimary = Color(0xFFF3EDFF),
        onBgSecondary = Color(0xFFB8A8D4),
        cardOverlay = Color(0xCC0A0514),
        glowColor = Color(0xFFC084FC)
    )

    val ADA = HeroTheme(
        heroId = "ada",
        backgroundRes = R.drawable.bg_ada,
        heroColor = Color(0xFFFF3855),         // vivid crimson for lanterns contrast
        heroBgColor = Color(0xFF1A0510),
        onBgPrimary = Color(0xFFF5E8E0),
        onBgSecondary = Color(0xFFB88D85),
        cardOverlay = Color(0xCC120508),
        glowColor = Color(0xFFFF6B88)
    )

    val LARA = HeroTheme(
        heroId = "lara_croft",
        backgroundRes = R.drawable.bg_lara,
        heroColor = Color(0xFF16A872),         // emerald
        heroBgColor = Color(0xFF052016),
        onBgPrimary = Color(0xFFFAF0D8),       // cream for warm torch light
        onBgSecondary = Color(0xFFBFA380),
        cardOverlay = Color(0xCC0A1810),
        glowColor = Color(0xFFFFA842)          // amber torch glow
    )

    val TWOB = HeroTheme(
        heroId = "twob",
        backgroundRes = R.drawable.bg_twob,
        heroColor = Color(0xFFCED6DE),         // cooler silver than the grey-slate brand color
        heroBgColor = Color(0xFF0A0A14),
        onBgPrimary = Color(0xFFEFF2F5),
        onBgSecondary = Color(0xFF8B939E),
        cardOverlay = Color(0xCC0C0C14),
        glowColor = Color(0xFFF0F4F8)          // pure white glow
    )

    val CIRI = HeroTheme(
        heroId = "ciri",
        backgroundRes = R.drawable.bg_ciri,
        heroColor = Color(0xFFE2E4E6),         // silver-white (signature ashy hair)
        heroBgColor = Color(0xFF0A1014),
        onBgPrimary = Color(0xFFE6EBEF),
        onBgSecondary = Color(0xFF8FA0AD),
        cardOverlay = Color(0xCC0A1014),
        glowColor = Color(0xFF7AEFBE)          // elder blood green
    )

    private val byId: Map<String, HeroTheme> = listOf(
        LEON, DANTE, KRATOS, SUNG_JINWOO, ADA, LARA, TWOB, CIRI
    ).associateBy { it.heroId }

    fun forHero(heroId: String?): HeroTheme = byId[heroId] ?: DefaultHeroTheme
}

/** Composable-scope access to the current HeroTheme. */
val LocalHeroTheme = compositionLocalOf { DefaultHeroTheme }

@Composable
@ReadOnlyComposable
fun heroTheme(): HeroTheme = LocalHeroTheme.current

/** Wraps children with the theme for [heroId]. */
@Composable
fun ProvideHeroTheme(heroId: String?, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalHeroTheme provides HeroThemes.forHero(heroId)) {
        content()
    }
}
