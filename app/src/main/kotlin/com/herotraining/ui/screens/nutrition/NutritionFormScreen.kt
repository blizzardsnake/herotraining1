package com.herotraining.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.Exclusion
import com.herotraining.data.model.FoodStyle
import com.herotraining.data.model.Hero
import com.herotraining.data.model.NutritionGoal
import com.herotraining.data.model.NutritionProfile
import com.herotraining.data.model.TreatKind
import com.herotraining.ui.components.HeroBackgroundScaffold
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.components.SelectButton
import com.herotraining.ui.components.StepProgress
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun NutritionFormScreen(
    hero: Hero,
    onBack: () -> Unit,
    onComplete: (NutritionProfile) -> Unit
) {
    var step by remember { mutableStateOf(0) }

    var style by remember { mutableStateOf<FoodStyle?>(null) }
    var exclusions by remember { mutableStateOf<Set<Exclusion>>(emptySet()) }
    var goal by remember { mutableStateOf<NutritionGoal?>(null) }
    var mealsPerDay by remember { mutableStateOf<Int?>(null) }
    var keepTreats by remember { mutableStateOf<Set<TreatKind>>(emptySet()) }

    val stepTitles = listOf(
        "Стиль питания" to null,
        "Исключения" to "Аллергии",
        "Цель" to null,
        "Приёмов/день" to null,
        "Слабости" to "Что НЕ убирать"
    )
    val isLast = step == stepTitles.lastIndex

    val canProceed = when (step) {
        0 -> style != null
        1 -> exclusions.isNotEmpty()
        2 -> goal != null
        3 -> mealsPerDay != null
        4 -> keepTreats.isNotEmpty()
        else -> false
    }

    val next = {
        if (isLast) {
            onComplete(
                NutritionProfile(
                    style = style!!,
                    exclusions = exclusions,
                    goal = goal!!,
                    mealsPerDay = mealsPerDay!!,
                    keepTreats = keepTreats
                )
            )
        } else step += 1
    }
    val prev = { if (step == 0) onBack() else step -= 1 }

    HeroBackgroundScaffold {
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
                modifier = Modifier.clickable { prev() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            StepProgress(current = step, total = stepTitles.size, activeColor = hero.color)
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Restaurant, contentDescription = null, tint = HeroPalette.Neutral500, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "ШАГ ${step + 1}/${stepTitles.size}",
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = stepTitles[step].first,
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = hero.color
                )
            )
            stepTitles[step].second?.let { subtitle ->
                Spacer(Modifier.height(4.dp))
                Text(subtitle, style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral400))
            }
            Spacer(Modifier.height(24.dp))

            when (step) {
                0 -> SingleSelect(
                    options = FoodStyle.entries.map { it to it.label },
                    selected = style, onSelect = { style = it }, color = hero.color
                )
                1 -> MultiSelect(
                    options = Exclusion.entries.map { it to it.label },
                    selected = exclusions,
                    onToggle = { v ->
                        exclusions = if (v == Exclusion.NONE) {
                            if (exclusions.contains(Exclusion.NONE)) emptySet() else setOf(Exclusion.NONE)
                        } else {
                            val without = exclusions - Exclusion.NONE
                            if (without.contains(v)) without - v else without + v
                        }
                    },
                    color = hero.color
                )
                2 -> SingleSelect(
                    options = NutritionGoal.entries.map { it to it.label },
                    selected = goal, onSelect = { goal = it }, color = hero.color
                )
                3 -> SingleSelect(
                    options = listOf(2 to "2 (IF)", 3 to "3", 4 to "4", 5 to "5"),
                    selected = mealsPerDay, onSelect = { mealsPerDay = it }, color = hero.color
                )
                4 -> MultiSelect(
                    options = TreatKind.entries.map { it to it.label },
                    selected = keepTreats,
                    onToggle = { v ->
                        keepTreats = if (v == TreatKind.NONE) {
                            if (keepTreats.contains(TreatKind.NONE)) emptySet() else setOf(TreatKind.NONE)
                        } else {
                            val without = keepTreats - TreatKind.NONE
                            if (without.contains(v)) without - v else without + v
                        }
                    },
                    color = hero.color
                )
            }

            Spacer(Modifier.height(24.dp))
            PrimaryOutlinedButton(
                text = if (isLast) "К ТЕСТУ →" else "ДАЛЕЕ →",
                accentColor = hero.color,
                onClick = next,
                enabled = canProceed
            )
        }
    }
}

@Composable
private fun <T> SingleSelect(options: List<Pair<T, String>>, selected: T?, onSelect: (T) -> Unit, color: androidx.compose.ui.graphics.Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (v, l) ->
            SelectButton(label = l, selected = selected == v, accentColor = color, onClick = { onSelect(v) })
        }
    }
}

@Composable
private fun <T> MultiSelect(options: List<Pair<T, String>>, selected: Set<T>, onToggle: (T) -> Unit, color: androidx.compose.ui.graphics.Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (v, l) ->
            SelectButton(label = l, selected = selected.contains(v), accentColor = color, onClick = { onToggle(v) })
        }
    }
}
