package com.herotraining.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.components.HeroBackgroundScaffold
import com.herotraining.ui.scifi.CornerBrackets
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani
import com.herotraining.ui.theme.heroTheme

/**
 * Small skeleton for tabs that are not fully implemented yet. Keeps the HUD aesthetic
 * so the app feels consistent even on "coming soon" screens.
 */
@Composable
fun TabPlaceholder(
    title: String,
    subtitle: String,
    nextRelease: String,
    icon: ImageVector = Icons.Filled.Construction
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
        }
    }
}

@Composable
fun WorkoutsTabScreen(onBackToHome: () -> Unit) {
    TabPlaceholder(
        title = "ТРЕНИРОВКИ",
        subtitle = "WORKOUT PROTOCOL",
        nextRelease = "v0.5.4 — план тренировок + combo-бар"
    )
}

@Composable
fun QuestsTabScreen() {
    TabPlaceholder(
        title = "КВЕСТЫ",
        subtitle = "DAILY PROTOCOL",
        nextRelease = "v0.5.2 — ежедневные квесты + signature + rewards"
    )
}

@Composable
fun ProgressTabScreen() {
    TabPlaceholder(
        title = "ПРОГРЕСС",
        subtitle = "BODY ANALYSIS",
        nextRelease = "v0.5.3 — графики тела, вес, before/after фото"
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
