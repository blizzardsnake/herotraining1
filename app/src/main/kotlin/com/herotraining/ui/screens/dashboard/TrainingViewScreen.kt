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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.catalog.HeroGearCatalog
import com.herotraining.data.model.UserState
import com.herotraining.domain.calc.calcVolume
import com.herotraining.domain.calc.getCurrentMicrocycle
import com.herotraining.domain.calc.getTodayWorkout
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun TrainingViewScreen(state: UserState, onBack: () -> Unit, onComplete: () -> Unit) {
    val hero = state.hero ?: return
    val build = state.build ?: return
    val baseline = state.baseline
    val cycle = getCurrentMicrocycle(state.programStartEpochMs)
    val workout = getTodayWorkout(state.programStartEpochMs, build)

    // Today's signature quest (only on Monday)
    val userGear = HeroGearCatalog.forHero(hero.id).filter { it.id in state.gear }
    val showSignature = userGear.isNotEmpty() && LocalDate.now().dayOfWeek == DayOfWeek.MONDAY
    val todaySignature = if (showSignature)
        userGear[LocalDate.now().dayOfMonth % userGear.size] else null

    val plan = buildList {
        if (baseline != null) {
            if (baseline.pushups > 0) add("Отжимания" to calcVolume(baseline.pushups, build, cycle))
            if (baseline.squats > 0) add("Приседания" to calcVolume(baseline.squats, build, cycle))
            if (baseline.plankSec > 0) add("Планка (сек)" to calcVolume(baseline.plankSec, build, cycle))
            if (baseline.pullups > 0) add("Подтягивания" to calcVolume(baseline.pullups, build, cycle))
        }
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
                Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = hero.color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = workout.name.uppercase(),
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 28.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
            }
            Text(
                text = workout.focus,
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )

            Spacer(Modifier.height(16.dp))

            if (todaySignature != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(hero.color.copy(alpha = 0.12f))
                        .border(2.dp, hero.color)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = hero.color, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "SIGNATURE QUEST",
                            style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold, color = hero.color)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = todaySignature.icon, style = TextStyle(fontSize = 26.sp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = todaySignature.signature,
                                style = TextStyle(fontFamily = ImpactLike, fontSize = 18.sp, fontWeight = FontWeight.Black, color = hero.color)
                            )
                            Text(
                                text = todaySignature.desc,
                                style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "+20% combo bonus",
                                style = TextStyle(fontSize = 10.sp, color = HeroPalette.Neutral500)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // Microcycle card
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, hero.color).padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = cycle.icon, style = TextStyle(fontSize = 20.sp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "НЕД ${cycle.weeks}",
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                        )
                        Text(
                            text = cycle.name,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = hero.color)
                        )
                        Text(
                            text = cycle.description,
                            style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("ПОВТОРЫ", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                        Text(cycle.repRange, style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = hero.color))
                    }
                    Column(Modifier.weight(1f)) {
                        Text("ОТДЫХ", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                        Text("${cycle.restSec}с", style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = hero.color))
                    }
                }
            }

            if (plan.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().border(2.dp, hero.color).padding(12.dp)
                ) {
                    Text(
                        text = "ПЕРСОНАЛЬНЫЙ ПЛАН",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = hero.color)
                    )
                    Spacer(Modifier.height(8.dp))
                    plan.forEach { (name, vol) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = name, style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300), modifier = Modifier.weight(1f))
                            Text(
                                text = "${vol.sets}×${vol.reps}",
                                style = TextStyle(fontFamily = ImpactLike, fontSize = 18.sp, fontWeight = FontWeight.Black, color = hero.color)
                            )
                        }
                        Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral900))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            PrimaryOutlinedButton(
                text = if (state.todayTrainingDone) "✓ ВЫПОЛНЕНО" else "✓ +15% COMBO",
                accentColor = hero.color,
                onClick = { if (!state.todayTrainingDone) { onComplete(); onBack() } },
                enabled = !state.todayTrainingDone
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "\"${build.philosophy}\"",
                style = TextStyle(fontSize = 11.sp, fontStyle = FontStyle.Italic, color = HeroPalette.Neutral500)
            )
        }
    }
}

