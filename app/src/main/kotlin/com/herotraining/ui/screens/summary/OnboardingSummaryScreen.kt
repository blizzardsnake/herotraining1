package com.herotraining.ui.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.catalog.HeroGearCatalog
import com.herotraining.data.model.Hero
import com.herotraining.data.model.HeroBuild
import com.herotraining.data.model.Profile
import com.herotraining.domain.calc.calcMacros
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import java.time.LocalTime

@Composable
fun OnboardingSummaryScreen(
    hero: Hero,
    build: HeroBuild,
    profile: Profile,
    gear: Set<String>,
    onContinue: () -> Unit
) {
    val macros = calcMacros(profile, build, hero)
    val unlockedGear = HeroGearCatalog.forHero(hero.id).filter { it.id in gear }
    val startsToday = LocalTime.now().hour < 10

    com.herotraining.ui.components.HeroBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 80.dp)
                .widthIn(max = 640.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ChecklistRtl, contentDescription = null, tint = hero.color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ПРОТОКОЛ ГОТОВ",
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 28.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
            }
            Text(
                text = "ТВОЯ ПЕРСОНАЛЬНАЯ ПРОГРАММА",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )

            Spacer(Modifier.height(20.dp))
            // Path card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, hero.color)
                    .padding(16.dp)
            ) {
                Text(
                    text = "ПУТЬ",
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = hero.color.copy(alpha = 0.7f))
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${hero.name} · ${build.name}",
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 22.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "\"${build.philosophy}\"",
                    style = TextStyle(fontSize = 11.sp, fontStyle = FontStyle.Italic, color = HeroPalette.Neutral400)
                )
            }

            // Unlocked gear
            if (unlockedGear.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(hero.color.copy(alpha = 0.08f))
                        .border(2.dp, hero.color)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Build, contentDescription = null, tint = hero.color, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "РАЗБЛОКИРОВАНО (${unlockedGear.size})",
                            style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = hero.color)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    unlockedGear.forEach { g ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(text = g.icon, style = TextStyle(fontSize = 18.sp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = g.signature,
                                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = hero.color)
                                )
                                Text(
                                    text = g.desc,
                                    style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Signature quests появятся раз в неделю в твоём плане.",
                        style = TextStyle(fontSize = 10.sp, fontStyle = FontStyle.Italic, color = HeroPalette.Neutral500)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            // Start time card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(hero.color.copy(alpha = 0.08f))
                    .border(1.dp, hero.color)
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Filled.WbSunny, contentDescription = null, tint = hero.color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "КОГДА СТАРТУЕТ КВЕСТ",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = hero.color)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (startsToday)
                            "Сегодня в 10:00, дедлайн 23:00."
                        else
                            "Сегодня — калибровка. Квест завтра в 10:00.",
                        style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            // Macros card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HeroPalette.Neutral800)
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Restaurant, contentDescription = null, tint = HeroPalette.Neutral500, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "МАКРОСЫ",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = macros.calories.toString(),
                        style = TextStyle(fontFamily = ImpactLike, fontSize = 38.sp, fontWeight = FontWeight.Black, color = hero.color)
                    )
                    Text(
                        text = "ККАЛ",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MacroTile(label = "Б", value = "${macros.protein}г", color = hero.color, modifier = Modifier.weight(1f))
                    MacroTile(label = "Ж", value = "${macros.fat}г", color = hero.color, modifier = Modifier.weight(1f))
                    MacroTile(label = "У", value = "${macros.carb}г", color = hero.color, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(24.dp))
            PrimaryOutlinedButton(
                text = "НАЧАТЬ →",
                accentColor = hero.color,
                onClick = onContinue
            )
        }
    }
}

@Composable
private fun MacroTile(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(
        modifier = modifier.border(1.dp, HeroPalette.Neutral900).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
        )
        Text(
            text = value,
            style = TextStyle(fontFamily = ImpactLike, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color, textAlign = TextAlign.Center)
        )
    }
}
