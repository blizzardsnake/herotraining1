package com.herotraining.ui.screens.profile

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.EquipmentKind
import com.herotraining.data.model.Experience
import com.herotraining.data.model.Gender
import com.herotraining.data.model.Hero
import com.herotraining.data.model.Injury
import com.herotraining.data.model.Profile
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.components.SelectButton
import com.herotraining.ui.components.StepProgress
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

/** 5-step onboarding form: basic → experience → equipment → time → injuries. */
@Composable
fun ProfileFormScreen(
    hero: Hero,
    gender: Gender,
    onBack: () -> Unit,
    onComplete: (Profile) -> Unit
) {
    var step by remember { mutableStateOf(0) }

    // Editable state
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf<Experience?>(null) }
    var equipment by remember { mutableStateOf<EquipmentKind?>(null) }
    var timePerSession by remember { mutableStateOf<Int?>(null) }
    var injuries by remember { mutableStateOf<Set<Injury>>(emptySet()) }

    val steps = listOf("Базовые данные", "Опыт", "Где тренируешься", "Время/сессию", "Травмы")
    val isLast = step == steps.lastIndex

    val canProceed: Boolean = when (step) {
        0 -> age.toIntOrNull() != null && weight.toIntOrNull() != null && height.toIntOrNull() != null
        1 -> experience != null
        2 -> equipment != null
        3 -> timePerSession != null
        4 -> injuries.isNotEmpty()
        else -> false
    }

    val next = {
        if (isLast) {
            onComplete(
                Profile(
                    age = age.toInt(),
                    weight = weight.toInt(),
                    height = height.toInt(),
                    sex = gender,
                    experience = experience!!,
                    equipment = equipment!!,
                    timePerSessionMinutes = timePerSession!!,
                    injuries = injuries
                )
            )
        } else {
            step += 1
        }
    }
    val prev = {
        if (step == 0) onBack() else step -= 1
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
                modifier = Modifier.clickable { prev() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            StepProgress(current = step, total = steps.size, activeColor = hero.color)
            Spacer(Modifier.height(20.dp))
            Text(
                text = "ШАГ ${step + 1}/${steps.size} · ПРОФИЛЬ",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = steps[step],
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = hero.color
                )
            )
            Spacer(Modifier.height(24.dp))

            when (step) {
                0 -> BasicDataStep(
                    age = age, weight = weight, height = height,
                    onAge = { age = it.filter { ch -> ch.isDigit() }.take(3) },
                    onWeight = { weight = it.filter { ch -> ch.isDigit() }.take(3) },
                    onHeight = { height = it.filter { ch -> ch.isDigit() }.take(3) },
                    accentColor = hero.color
                )
                1 -> SelectStep(
                    options = Experience.entries.map { it to it.label },
                    selected = experience,
                    onSelect = { experience = it },
                    accentColor = hero.color
                )
                2 -> EquipmentStep(
                    selected = equipment,
                    onSelect = { equipment = it },
                    accentColor = hero.color
                )
                3 -> SelectStep(
                    options = listOf(30, 45, 60, 90).map { it to "$it мин" },
                    selected = timePerSession,
                    onSelect = { timePerSession = it },
                    accentColor = hero.color
                )
                4 -> {
                    Text(
                        text = "Отметь всё",
                        style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    MultiSelectStep(
                        options = Injury.entries.map { it to it.label },
                        selected = injuries,
                        onToggle = { v ->
                            injuries = if (v == Injury.NONE) {
                                if (injuries.contains(Injury.NONE)) emptySet() else setOf(Injury.NONE)
                            } else {
                                val without = injuries - Injury.NONE
                                if (without.contains(v)) without - v else without + v
                            }
                        },
                        accentColor = hero.color
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            PrimaryOutlinedButton(
                text = if (isLast) "К СНАРЯЖЕНИЮ →" else "ДАЛЕЕ →",
                accentColor = hero.color,
                onClick = next,
                enabled = canProceed
            )
        }
    }
}

@Composable
private fun BasicDataStep(
    age: String, weight: String, height: String,
    onAge: (String) -> Unit, onWeight: (String) -> Unit, onHeight: (String) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NumericField(label = "ВОЗРАСТ", value = age, onChange = onAge, suffix = "лет", accentColor = accentColor)
        NumericField(label = "ВЕС", value = weight, onChange = onWeight, suffix = "кг", accentColor = accentColor)
        NumericField(label = "РОСТ", value = height, onChange = onHeight, suffix = "см", accentColor = accentColor)
    }
}

@Composable
private fun NumericField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    suffix: String,
    accentColor: Color
) {
    Column {
        Text(
            text = label,
            style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = value,
                onValueChange = onChange,
                placeholder = { Text("0", color = HeroPalette.Neutral700) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 20.sp, color = accentColor, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, HeroPalette.Neutral800),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = accentColor
                )
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = suffix,
                style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.width(36.dp)
            )
        }
    }
}

@Composable
private fun <T> SelectStep(
    options: List<Pair<T, String>>,
    selected: T?,
    onSelect: (T) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, label) ->
            SelectButton(
                label = label,
                selected = selected == value,
                accentColor = accentColor,
                onClick = { onSelect(value) }
            )
        }
    }
}

@Composable
private fun EquipmentStep(
    selected: EquipmentKind?,
    onSelect: (EquipmentKind) -> Unit,
    accentColor: Color
) {
    val iconFor = { e: EquipmentKind ->
        when (e) {
            EquipmentKind.GYM -> Icons.Filled.Apartment
            EquipmentKind.HOME_FULL -> Icons.Filled.FitnessCenter
            EquipmentKind.HOME_LIGHT -> Icons.Filled.Home
            EquipmentKind.BODYWEIGHT -> Icons.Filled.LocationOn
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EquipmentKind.entries.forEach { eq ->
            val sel = selected == eq
            SelectButton(
                label = eq.label,
                selected = sel,
                accentColor = accentColor,
                onClick = { onSelect(eq) },
                leading = {
                    Icon(
                        imageVector = iconFor(eq),
                        contentDescription = null,
                        tint = if (sel) accentColor else HeroPalette.Neutral600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun <T> MultiSelectStep(
    options: List<Pair<T, String>>,
    selected: Set<T>,
    onToggle: (T) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, label) ->
            SelectButton(
                label = label,
                selected = selected.contains(value),
                accentColor = accentColor,
                onClick = { onToggle(value) }
            )
        }
    }
}
