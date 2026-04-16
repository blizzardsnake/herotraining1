package com.herotraining.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.catalog.HeroGearCatalog
import com.herotraining.data.model.UserState
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun GearViewScreen(state: UserState, onBack: () -> Unit, onSave: (Set<String>) -> Unit) {
    val hero = state.hero ?: return
    val gearList = remember(hero.id) { HeroGearCatalog.forHero(hero.id) }
    var selected by remember { mutableStateOf(state.gear) }
    val toggle: (String) -> Unit = { id ->
        selected = if (selected.contains(id)) selected - id else selected + id
    }

    Box(Modifier.fillMaxSize().background(hero.bgColor)) {
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
                Icon(Icons.Filled.Build, contentDescription = null, tint = hero.color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "СНАРЯЖЕНИЕ",
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 28.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
            }
            Text(
                text = "СНАРЯЖЕНИЕ · ${hero.name}",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(14.dp))
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(12.dp)
            ) {
                Text(
                    text = "Отмечай/снимай что актуально. Signature quests используют активные модули.",
                    style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
                )
            }
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                gearList.forEach { g ->
                    val isSel = g.id in selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSel) hero.color.copy(alpha = 0.1f) else Color.Transparent)
                            .border(1.dp, if (isSel) hero.color else HeroPalette.Neutral700)
                            .clickable { toggle(g.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = g.icon, style = TextStyle(fontSize = 20.sp))
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = g.label,
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSel) hero.color else HeroPalette.Neutral300)
                            )
                            Text(
                                text = g.signature,
                                style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = if (isSel) hero.color else HeroPalette.Neutral600)
                            )
                            Text(text = g.desc, style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500))
                        }
                        if (isSel) Icon(Icons.Filled.Check, contentDescription = null, tint = hero.color, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            PrimaryOutlinedButton(
                text = "СОХРАНИТЬ · ${selected.size} АКТИВНЫХ",
                accentColor = hero.color,
                onClick = { onSave(selected); onBack() }
            )
        }
    }
}
