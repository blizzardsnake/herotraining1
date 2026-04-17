package com.herotraining.ui.tabs

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.herotraining.R

/**
 * 5-tab bottom navigation using pre-rendered artwork from the QUESTS mockup.
 *
 * Each tab is a 188×168 WebP crop (see scripts/slice_quests.py). They sit in a Row
 * as equal-weight cells at the bottom of the Scaffold. Press = scale 0.92 for tactile
 * feedback. The tab artwork is static for now (KBECTbI tab shows as active in every
 * position because that's what the source mockup had) — we'll swap in per-screen
 * active variants when the other mockups get regenerated.
 */
@Composable
fun MainTabsHost(
    onReset: () -> Unit,
    onHardReset: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var tab by remember { mutableStateOf(TabKind.HOME) }

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            HeroBottomNav(
                selected = tab,
                onSelect = { tab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (tab) {
                TabKind.HOME -> HomeTabScreen(
                    onReset = onReset,
                    onOpenWorkouts = { tab = TabKind.WORKOUTS },
                    onOpenProgress = { tab = TabKind.PROGRESS },
                    onOpenProfile = { tab = TabKind.PROFILE }
                )
                TabKind.WORKOUTS -> WorkoutsTabScreen(onBackToHome = { tab = TabKind.HOME })
                TabKind.QUESTS -> QuestsTabScreen()
                TabKind.PROGRESS -> ProgressTabScreen()
                TabKind.PROFILE -> ProfileTabScreen(
                    onReset = onReset,
                    onHardReset = onHardReset,
                    onSignOut = onSignOut
                )
            }
        }
    }
}

/**
 * Bottom nav bar — 5 equal-weight tab cells, each is a clickable Image.
 * The whole row respects the system gesture-bar inset via navigationBarsPadding.
 */
@Composable
private fun HeroBottomNav(
    selected: TabKind,
    onSelect: (TabKind) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TabKind.entries.forEach { kind ->
            NavTabButton(
                imageRes = kind.drawable,
                contentDescription = kind.label,
                onClick = { onSelect(kind) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Single tab — pressable Image that scales down on finger-press.
 * Uses the source crop's aspect ratio (188:168) so the whole row gets a consistent
 * height regardless of screen width.
 */
@Composable
private fun NavTabButton(
    @DrawableRes imageRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        label = "nav-tab-scale"
    )
    Image(
        painter = painterResource(imageRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .aspectRatio(188f / 168f)
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
    )
}

enum class TabKind(val label: String, @DrawableRes val drawable: Int) {
    HOME("ГЛАВНАЯ", R.drawable.nav_tab_home),
    WORKOUTS("ТРЕНИРОВКИ", R.drawable.nav_tab_workouts),
    QUESTS("КВЕСТЫ", R.drawable.nav_tab_quests),
    PROGRESS("ПРОГРЕСС", R.drawable.nav_tab_progress),
    PROFILE("ПРОФИЛЬ", R.drawable.nav_tab_profile)
}

typealias MainTab = TabKind
