package com.herotraining.ui.screens.gear

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
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
import com.herotraining.data.model.Hero
import com.herotraining.data.model.HeroGear
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun HeroGearFormScreen(
    hero: Hero,
    onBack: () -> Unit,
    onComplete: (Set<String>) -> Unit
) {
    val gearList = HeroGearCatalog.forHero(hero.id)
    var selected by remember { mutableStateOf<Set<String>>(emptySet()) }
    val toggle: (String) -> Unit = { id ->
        selected = if (selected.contains(id)) selected - id else selected + id
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(hero.bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 80.dp)
                .widthIn(max = 640.dp)
        ) {
            Text(
                text = "← НАЗАД",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Build, contentDescription = null, tint = hero.color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "СНАРЯЖЕНИЕ",
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = hero.color
                    )
                )
            }
            Text(
                text = "${hero.name} · СНАРЯЖЕНИЕ ГЕРОЯ",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )

            Spacer(Modifier.height(16.dp))
            // Info bloc
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, hero.color)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = hero.color, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "ЧТО ЭТО",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = hero.color)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Отметь к чему у тебя есть регулярный доступ. Это не обязательно — базовая программа работает для всех.",
                    style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Каждый доступ открывает фирменную тренировку ${hero.name} — раз в неделю.",
                    style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
                )
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "ТВОЁ СНАРЯЖЕНИЕ:",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                gearList.forEach { gear ->
                    GearRow(gear = gear, selected = selected.contains(gear.id), accentColor = hero.color) {
                        toggle(gear.id)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Можешь вернуться и отметить позже, если появится доступ.",
                style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            )
            Spacer(Modifier.height(16.dp))

            PrimaryOutlinedButton(
                text = if (selected.isNotEmpty()) "ПРОДОЛЖИТЬ · ${selected.size} РАЗБЛОКИРОВАНО →"
                       else "ПРОПУСТИТЬ →",
                accentColor = hero.color,
                onClick = { onComplete(selected) }
            )
        }
    }
}

@Composable
private fun GearRow(
    gear: HeroGear,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val borderColor = when {
        selected -> accentColor
        gear.featured -> accentColor.copy(alpha = 0.5f)
        else -> HeroPalette.Neutral700
    }
    val bg = if (selected) accentColor.copy(alpha = 0.1f) else Color.Transparent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .border(1.dp, borderColor)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = gear.icon, style = TextStyle(fontSize = 22.sp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = gear.label,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) accentColor else HeroPalette.Neutral300
                    )
                )
                if (gear.featured) {
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Filled.Star, contentDescription = null, tint = accentColor, modifier = Modifier.size(11.dp))
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = "ОТКРЫВАЕТ: ${gear.signature}",
                style = TextStyle(
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = if (selected) accentColor else HeroPalette.Neutral600
                )
            )
            Text(
                text = gear.desc,
                style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)
            )
            if (gear.featured && !selected) {
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "⭐ КЛЮЧЕВОЙ НАВЫК ГЕРОЯ",
                    style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = accentColor.copy(alpha = 0.8f))
                )
            }
        }
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
        }
    }
}
