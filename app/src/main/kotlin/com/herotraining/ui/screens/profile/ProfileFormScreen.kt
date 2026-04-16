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
import com.herotraining.data.model.Injury
import com.herotraining.data.model.Profile
import com.herotraining.domain.calc.BmiResult
import com.herotraining.domain.calc.calcBmi
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.components.SelectButton
import com.herotraining.ui.components.StepProgress
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

/**
 * 6-step onboarding anketa:
 *   0: sex + age + height + weight (with BMI feedback)
 *   1: experience
 *   2: equipment
 *   3: time per session
 *   4: injuries
 */
@Composable
fun ProfileFormScreen(
    onBack: () -> Unit,
    onComplete: (Profile) -> Unit
) {
    var step by remember { mutableStateOf(0) }

    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf<Gender?>(null) }
    var experience by remember { mutableStateOf<Experience?>(null) }
    var equipment by remember { mutableStateOf<EquipmentKind?>(null) }
    var timePerSession by remember { mutableStateOf<Int?>(null) }
    var injuries by remember { mutableStateOf<Set<Injury>>(emptySet()) }

    val stepTitles = listOf(
        "Базовые данные", "Опыт", "Где тренируешься", "Время на сессию", "Травмы"
    )
    val isLast = step == stepTitles.lastIndex
    val accent = HeroPalette.Red500   // anketa uses the brand red, hero-specific colors come later

    val canProceed: Boolean = when (step) {
        0 -> sex != null && age.toIntOrNull() != null && weight.toIntOrNull() != null && height.toIntOrNull() != null
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
                    sex = sex!!,
                    experience = experience!!,
                    equipment = equipment!!,
                    timePerSessionMinutes = timePerSession!!,
                    injuries = injuries
                )
            )
        } else step += 1
    }
    val prev = { if (step == 0) onBack() else step -= 1 }

    Box(Modifier.fillMaxSize().background(HeroPalette.Black)) {
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
            Spacer(Modifier.height(12.dp))
            // Badge
            Box(
                modifier = Modifier.border(1.dp, accent).padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ПРОТОКОЛ ГЕРОЯ · АНКЕТА",
                    style = TextStyle(color = accent, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Medium)
                )
            }
            Spacer(Modifier.height(16.dp))
            StepProgress(current = step, total = stepTitles.size, activeColor = accent)
            Spacer(Modifier.height(18.dp))
            Text(
                text = "ШАГ ${step + 1} ИЗ ${stepTitles.size}",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stepTitles[step],
                style = TextStyle(fontFamily = ImpactLike, fontSize = 26.sp, fontWeight = FontWeight.Black, color = accent)
            )
            Spacer(Modifier.height(22.dp))

            when (step) {
                0 -> BasicDataStep(
                    sex = sex, age = age, height = height, weight = weight,
                    onSex = { sex = it },
                    onAge = { age = it.filter(Char::isDigit).take(3) },
                    onHeight = { height = it.filter(Char::isDigit).take(3) },
                    onWeight = { weight = it.filter(Char::isDigit).take(3) },
                    accent = accent
                )
                1 -> SingleSelectRows(
                    options = Experience.entries.map { it to it.label },
                    selected = experience, onSelect = { experience = it }, accent = accent
                )
                2 -> EquipmentStep(selected = equipment, onSelect = { equipment = it }, accent = accent)
                3 -> SingleSelectRows(
                    options = listOf(30, 45, 60, 90).map { it to "$it минут" },
                    selected = timePerSession, onSelect = { timePerSession = it }, accent = accent
                )
                4 -> {
                    Text(
                        text = "Отметь всё что есть",
                        style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    MultiSelectRows(
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
                        accent = accent
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            PrimaryOutlinedButton(
                text = if (isLast) "К ВЫБОРУ ГЕРОЯ →" else "ДАЛЕЕ →",
                accentColor = accent,
                onClick = next,
                enabled = canProceed
            )
        }
    }
}

@Composable
private fun BasicDataStep(
    sex: Gender?, age: String, height: String, weight: String,
    onSex: (Gender) -> Unit,
    onAge: (String) -> Unit, onHeight: (String) -> Unit, onWeight: (String) -> Unit,
    accent: Color
) {
    Column {
        // Sex
        Text(
            text = "ПОЛ",
            style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SexTile(label = "МУЖСКОЙ", selected = sex == Gender.MALE, accent = accent, modifier = Modifier.weight(1f)) { onSex(Gender.MALE) }
            SexTile(label = "ЖЕНСКИЙ", selected = sex == Gender.FEMALE, accent = accent, modifier = Modifier.weight(1f)) { onSex(Gender.FEMALE) }
        }
        Spacer(Modifier.height(16.dp))

        NumericField(label = "ВОЗРАСТ", value = age, onChange = onAge, suffix = "лет", accent = accent)
        Spacer(Modifier.height(12.dp))
        NumericField(label = "РОСТ", value = height, onChange = onHeight, suffix = "см", accent = accent)
        Spacer(Modifier.height(12.dp))
        NumericField(label = "ВЕС", value = weight, onChange = onWeight, suffix = "кг", accent = accent)

        // BMI card — appears once all three numerics are filled
        val bmi: BmiResult? = calcBmi(weight.toIntOrNull(), height.toIntOrNull())
        if (bmi != null) {
            Spacer(Modifier.height(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bmi.category.color.copy(alpha = 0.08f))
                    .border(2.dp, bmi.category.color)
                    .padding(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ИНДЕКС МАССЫ ТЕЛА",
                            style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = bmi.category.color)
                        )
                        Text(
                            text = bmi.value.toString(),
                            style = TextStyle(fontFamily = ImpactLike, fontSize = 34.sp, fontWeight = FontWeight.Black, color = bmi.category.color)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "КАТЕГОРИЯ",
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                        )
                        Text(
                            text = bmi.category.label,
                            style = TextStyle(fontFamily = ImpactLike, fontSize = 18.sp, fontWeight = FontWeight.Black, color = bmi.category.color)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "НОРМА ДЛЯ ТЕБЯ: ${bmi.idealMin}–${bmi.idealMax} кг",
                    style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = bmiAdviceFor(bmi),
                    style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300)
                )
            }
        }
    }
}

private fun bmiAdviceFor(b: BmiResult): String = when (b.category) {
    com.herotraining.domain.calc.BmiCategory.UNDERWEIGHT ->
        "Недобор. Делаем акцент на питание и силовой набор массы."
    com.herotraining.domain.calc.BmiCategory.NORMAL ->
        "Отличная база. Работаем на выносливость и рельеф."
    com.herotraining.domain.calc.BmiCategory.OVERWEIGHT ->
        "Небольшой избыток. Кардио + дефицит калорий ~10%."
    com.herotraining.domain.calc.BmiCategory.OBESE_1 ->
        "Начинаем с низкоударных тренировок + дефицит ~15%."
    com.herotraining.domain.calc.BmiCategory.OBESE_2 ->
        "Стартуем с ходьбы и бассейна. Консультация врача рекомендована."
    com.herotraining.domain.calc.BmiCategory.OBESE_3 ->
        "Программа требует медицинского сопровождения. Начинаем мягко."
}

@Composable
private fun SexTile(label: String, selected: Boolean, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    val borderColor = if (selected) accent else HeroPalette.Neutral700
    val bg = if (selected) accent.copy(alpha = 0.12f) else Color.Transparent
    Box(
        modifier = modifier
            .background(bg)
            .border(2.dp, borderColor)
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = ImpactLike,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 3.sp,
                color = if (selected) accent else HeroPalette.Neutral400
            )
        )
    }
}

@Composable
private fun NumericField(
    label: String, value: String, onChange: (String) -> Unit, suffix: String, accent: Color
) {
    Column {
        Text(
            text = label,
            style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = value,
                onValueChange = onChange,
                placeholder = { Text("0", color = HeroPalette.Neutral700) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 20.sp, color = accent, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f).border(1.dp, HeroPalette.Neutral800),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = accent
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
private fun <T> SingleSelectRows(
    options: List<Pair<T, String>>, selected: T?, onSelect: (T) -> Unit, accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (v, label) ->
            SelectButton(label = label, selected = selected == v, accentColor = accent, onClick = { onSelect(v) })
        }
    }
}

@Composable
private fun <T> MultiSelectRows(
    options: List<Pair<T, String>>, selected: Set<T>, onToggle: (T) -> Unit, accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (v, label) ->
            SelectButton(label = label, selected = selected.contains(v), accentColor = accent, onClick = { onToggle(v) })
        }
    }
}

@Composable
private fun EquipmentStep(selected: EquipmentKind?, onSelect: (EquipmentKind) -> Unit, accent: Color) {
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
                accentColor = accent,
                onClick = { onSelect(eq) },
                leading = {
                    Icon(
                        imageVector = iconFor(eq),
                        contentDescription = null,
                        tint = if (sel) accent else HeroPalette.Neutral600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}
