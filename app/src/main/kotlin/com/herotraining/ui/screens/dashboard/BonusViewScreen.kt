package com.herotraining.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.UserState
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun BonusViewScreen(state: UserState, onBack: () -> Unit, onComplete: () -> Unit) {
    val hero = state.hero ?: return
    com.herotraining.ui.components.HeroBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 80.dp)
                .widthIn(max = 640.dp)
        ) {
            Text(
                text = "← ДАШБОРД",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Whatshot, contentDescription = null, tint = hero.color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "БОНУС",
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 28.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
            }
            Text(
                text = hero.bonusQuest.title,
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(16.dp)
            ) {
                Text(
                    text = hero.bonusQuest.desc,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = hero.color)
                )
            }
            Spacer(Modifier.height(20.dp))
            PrimaryOutlinedButton(
                text = if (state.todayBonusDone) "✓ ВЫПОЛНЕНО" else "✓ +8% COMBO",
                accentColor = hero.color,
                onClick = { if (!state.todayBonusDone) { onComplete(); onBack() } },
                enabled = !state.todayBonusDone
            )
        }
    }
}
