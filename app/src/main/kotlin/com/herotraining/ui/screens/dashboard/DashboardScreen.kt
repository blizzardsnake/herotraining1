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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.herotraining.data.model.UserState
import com.herotraining.domain.calc.calcMacros
import com.herotraining.domain.calc.getCurrentMicrocycle
import com.herotraining.domain.calc.getCurrentRank
import com.herotraining.domain.calc.getDecayedCombo
import com.herotraining.domain.calc.getTodayWorkout
import com.herotraining.ui.components.ComboBar
import com.herotraining.ui.components.QuestWindow
import com.herotraining.ui.components.StatTile
import com.herotraining.ui.components.UpdateBanner
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun DashboardScreen(
    onTraining: () -> Unit,
    onNutrition: () -> Unit,
    onBonus: () -> Unit,
    onGear: () -> Unit,
    onReset: () -> Unit,
    vm: DashboardViewModel = viewModel()
) {
    val state: UserState by vm.state.collectAsStateWithLifecycle()
    val hero = state.hero
    val build = state.build
    val profile = state.profile

    if (hero == null || build == null || profile == null) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text(
                text = "ЗАГРУЗКА СОСТОЯНИЯ...",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
        }
        return
    }

    val combo = getDecayedCombo(state)
    val rank = getCurrentRank(hero, state.rankPoints)
    val cycle = getCurrentMicrocycle(state.programStartEpochMs)
    val workout = getTodayWorkout(state.programStartEpochMs, build)
    val macros = calcMacros(profile, build, hero)
    val totalKcal = state.todayMeals.sumOf { it.kcal }

    Box(Modifier.fillMaxSize().background(hero.bgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HeroPalette.Neutral900)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hero.name,
                        style = TextStyle(fontFamily = ImpactLike, fontSize = 18.sp, fontWeight = FontWeight.Black, color = hero.color)
                    )
                    Text(
                        text = build.name,
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = hero.color.copy(alpha = 0.7f))
                    )
                }
                Text(
                    text = "СМЕНИТЬ",
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral600),
                    modifier = Modifier.clickable { onReset() }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
                    .widthIn(max = 720.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Update banner (shown when newer release found on GitHub)
                val updateState by vm.update.collectAsStateWithLifecycle()
                UpdateBanner(
                    state = updateState,
                    accent = hero.color,
                    onDownload = { vm.downloadUpdate(it) },
                    onInstall = { vm.launchInstaller() }
                )

                ComboBar(combo = combo, hero = hero)

                // Rank tile
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, hero.color)
                        .padding(16.dp)
                ) {
                    Text(
                        text = hero.rankSystem.name,
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = hero.color.copy(alpha = 0.7f))
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = rank.rank,
                            style = TextStyle(fontFamily = ImpactLike, fontSize = 56.sp, fontWeight = FontWeight.Black, color = hero.color)
                        )
                        if (rank.label != rank.rank) {
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = rank.label,
                                style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = hero.color),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                    if (rank.nextRank != null) {
                        Box(Modifier.fillMaxWidth().height(4.dp).background(HeroPalette.Neutral900)) {
                            Box(
                                Modifier
                                    .fillMaxWidth(rank.progress)
                                    .height(4.dp)
                                    .background(hero.color)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "${state.rankPoints} RP",
                                style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                            )
                            Text(
                                text = "СЛЕД: ${rank.nextRank}",
                                style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                            )
                        }
                    }
                }

                // Hero Gear banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HeroPalette.Neutral800)
                        .clickable { onGear() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, tint = hero.color, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "СНАРЯЖЕНИЕ",
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                        )
                        Text(
                            text = if (state.gear.isNotEmpty()) "${state.gear.size} активных"
                                   else "НЕ НАСТРОЕНО",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = hero.color)
                        )
                    }
                    Text(text = "›", style = TextStyle(fontSize = 18.sp, color = HeroPalette.Neutral500))
                }

                // Microcycle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HeroPalette.Neutral800)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = cycle.icon, style = TextStyle(fontSize = 20.sp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "НЕД ${cycle.weeks}",
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                        )
                        Text(
                            text = cycle.name,
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = hero.color)
                        )
                    }
                }

                // Stats row
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatTile(
                        icon = Icons.Filled.LocalFireDepartment, label = "СЕРИЯ",
                        value = state.streak.toString(), accent = hero.color, modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        icon = Icons.Filled.MonetizationOn, label = "МОНЕТЫ",
                        value = state.coins.toString(), accent = hero.color, modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        icon = Icons.Filled.Star, label = "ДОСТ.",
                        value = state.achievements.size.toString(), accent = hero.color, modifier = Modifier.weight(1f)
                    )
                }

                val steps by vm.steps.collectAsStateWithLifecycle()
                if (steps > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.DirectionsWalk, contentDescription = null, tint = hero.color, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ШАГИ ЗА СЕГОДНЯ",
                                style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                            Text("$steps",
                                style = TextStyle(fontFamily = ImpactLike, fontSize = 20.sp, fontWeight = FontWeight.Black, color = hero.color))
                        }
                    }
                }

                QuestWindow(hero = hero, build = build, state = state)

                // Quick action rows
                ActionRow(
                    icon = Icons.Filled.FitnessCenter,
                    title = "Тренировка · ${workout.name}",
                    subtitle = if (state.todayTrainingDone) "ВЫПОЛНЕНО" else workout.focus,
                    done = state.todayTrainingDone,
                    accent = hero.color,
                    onClick = onTraining
                )
                ActionRow(
                    icon = Icons.Filled.Restaurant,
                    title = "Питание · $totalKcal/${macros.calories} ккал",
                    subtitle = if (state.todayNutritionDone) "ВЫПОЛНЕНО" else "${state.todayMeals.size} приёмов",
                    done = state.todayNutritionDone,
                    accent = hero.color,
                    onClick = onNutrition
                )
                ActionRow(
                    icon = Icons.Filled.Whatshot,
                    title = hero.bonusQuest.title,
                    subtitle = if (state.todayBonusDone) "ВЫПОЛНЕНО" else "БОНУС",
                    done = state.todayBonusDone,
                    accent = hero.color,
                    onClick = onBonus
                )

                // Philosophy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.width(2.dp).height(28.dp).background(hero.color))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "\"${build.philosophy}\"",
                        style = TextStyle(fontSize = 12.sp, fontStyle = FontStyle.Italic, color = HeroPalette.Neutral400)
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "v0.8 · ПРОТОКОЛ АКТИВЕН",
                    style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral700),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    done: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (done) accent.copy(alpha = 0.5f) else HeroPalette.Neutral800)
            .clickable(enabled = !done, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, if (done) accent else HeroPalette.Neutral700),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (done) Icons.Filled.Check else icon,
                contentDescription = null,
                tint = if (done) accent else HeroPalette.Neutral400,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (done) HeroPalette.Neutral400 else Color.White)
            )
            Text(
                text = subtitle,
                style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
            )
        }
    }
}
