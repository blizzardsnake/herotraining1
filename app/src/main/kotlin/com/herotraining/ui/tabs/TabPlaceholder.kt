package com.herotraining.ui.tabs

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.R
import com.herotraining.ui.components.HeroBackgroundScaffold
import com.herotraining.ui.scifi.CornerBrackets
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani
import com.herotraining.ui.theme.heroTheme

/**
 * Small skeleton for tabs that are not fully implemented yet. Keeps the HUD aesthetic
 * so the app feels consistent even on "coming soon" screens.
 *
 * @param ctaDrawable Optional sliced CTA image (from mockups) shown as the primary button.
 *                    Clicking triggers [onCtaClick]. This lets us visually populate
 *                    placeholder tabs with the finished art even before the real screen is
 *                    built out.
 * @param ctaWidthFraction How much of the screen width the CTA should occupy (0..1).
 *                         Defaults to 0.85 — big pill buttons look right; small pill buttons
 *                         should be passed a smaller fraction like 0.45.
 * @param ctaAspectRatio   width/height of the source crop. Used to compute natural height.
 */
@Composable
fun TabPlaceholder(
    title: String,
    subtitle: String,
    nextRelease: String,
    icon: ImageVector = Icons.Filled.Construction,
    @DrawableRes ctaDrawable: Int? = null,
    ctaWidthFraction: Float = 0.85f,
    ctaAspectRatio: Float = 860f / 95f,
    onCtaClick: (() -> Unit)? = null
) {
    val th = heroTheme()
    HeroBackgroundScaffold {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                CornerBrackets(color = th.heroColor, armLength = 20.dp, thickness = 2.dp)
                Icon(icon, null, tint = th.heroColor, modifier = Modifier.size(64.dp).padding(16.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = title,
                style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Color.White, letterSpacing = 2.sp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(30.dp))
            Text(
                text = "СЛЕДУЮЩИЙ РЕЛИЗ",
                style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 3.sp, color = th.heroColor.copy(alpha = 0.6f))
            )
            Text(
                text = nextRelease,
                style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = th.heroColor)
            )

            if (ctaDrawable != null) {
                Spacer(Modifier.height(36.dp))
                PressableImageButton(
                    resId = ctaDrawable,
                    aspectRatio = ctaAspectRatio,
                    onClick = onCtaClick ?: {},
                    modifier = Modifier.fillMaxWidth(ctaWidthFraction)
                )
            }
        }
    }
}

/**
 * Thin reusable wrapper: Image that scales down on finger-press and runs the click action.
 * Used for any sliced button asset — nav tabs, CTAs, mockup buttons.
 */
@Composable
private fun PressableImageButton(
    @DrawableRes resId: Int,
    aspectRatio: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        label = "pressable-scale"
    )
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .then(
                if (aspectRatio > 0f) Modifier else Modifier
            )
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
    )
}

// WorkoutsTabScreen теперь живёт в ui/tabs/workouts/WorkoutsTabScreen.kt —
// настоящая тренировка (v0.9+). MainTabsHost импортирует оттуда.

@Composable
fun QuestsTabScreen(onBackToHome: () -> Unit = {}) {
    TabPlaceholder(
        title = "КВЕСТЫ",
        subtitle = "DAILY PROTOCOL",
        nextRelease = "v0.6 — ежедневные квесты + signature + rewards",
        ctaDrawable = R.drawable.btn_quests_continue,
        ctaWidthFraction = 0.92f,
        ctaAspectRatio = 860f / 115f,
        onCtaClick = onBackToHome
    )
}

@Composable
fun ProgressTabScreen(onBackToHome: () -> Unit = {}) {
    TabPlaceholder(
        title = "ПРОГРЕСС",
        subtitle = "BODY ANALYSIS",
        nextRelease = "v0.6 — графики тела, вес, before/after фото",
        ctaDrawable = R.drawable.btn_progress_add,
        ctaWidthFraction = 0.92f,
        ctaAspectRatio = 860f / 95f,
        onCtaClick = onBackToHome
    )
}

@Composable
fun ProfileTabScreen(
    onReset: () -> Unit,
    onHardReset: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    com.herotraining.ui.screens.profile_view.ProfileScreen(
        onBack = { /* this is a tab now, no back */ },
        onHardReset = onHardReset,
        onSignOut = onSignOut
    )
}
