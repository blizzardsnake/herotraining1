package com.herotraining.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.EquipmentKind
import com.herotraining.data.model.Experience
import com.herotraining.data.model.Gender
import com.herotraining.data.model.Injury
import com.herotraining.data.model.Profile
import com.herotraining.domain.calc.BmiCategory
import com.herotraining.domain.calc.calcBmi
import com.herotraining.ui.scifi.CornerBrackets
import com.herotraining.ui.scifi.SciFiCard
import com.herotraining.ui.scifi.SciFiGhostButton
import com.herotraining.ui.scifi.SciFiHeader
import com.herotraining.ui.scifi.SciFiNumericField
import com.herotraining.ui.scifi.SciFiPrimaryButton
import com.herotraining.ui.scifi.SciFiStepProgress
import com.herotraining.ui.scifi.SciFiTopBar
import com.herotraining.ui.scifi.SystemAnalysisPanel
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani

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
        "БАЗОВЫЕ ДАННЫЕ" to "PRIMARY PARAMETERS",
        "ОПЫТ" to "EXPERIENCE LEVEL",
        "ГДЕ ТРЕНИРУЕШЬСЯ" to "ENVIRONMENT",
        "ВРЕМЯ НА СЕССИЮ" to "SESSION LENGTH",
        "ТРАВМЫ" to "INJURY FLAGS"
    )
    val isLast = step == stepTitles.lastIndex
    val accent = HeroPalette.Red500

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
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            SciFiTopBar(
                title = "Протокол инициализации",
                onBack = prev,
                rightStatus = "POWER: UNDEFINED",
                accent = accent
            )
            SciFiStepProgress(
                current = step,
                total = stepTitles.size,
                accent = accent,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Column(
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 80.dp)
                    .widthIn(max = 640.dp)
            ) {
                Text(
                    text = "ШАГ ${String.format("%02d", step + 1)} // ${String.format("%02d", stepTitles.size)}",
                    style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 3.sp, color = accent.copy(alpha = 0.7f))
                )
                Spacer(Modifier.height(4.dp))
                SciFiHeader(
                    title = stepTitles[step].first,
                    subtitle = stepTitles[step].second,
                    accent = accent,
                    onBg = Color.White,
                    titleFontSize = 34.sp
                )
                Spacer(Modifier.height(20.dp))

                when (step) {
                    0 -> BasicDataStep(
                        sex = sex, age = age, height = height, weight = weight,
                        onSex = { sex = it },
                        onAge = { age = it.filter(Char::isDigit).take(3) },
                        onHeight = { height = it.filter(Char::isDigit).take(3) },
                        onWeight = { weight = it.filter(Char::isDigit).take(3) },
                        accent = accent
                    )
                    1 -> SingleSelectStep(
                        options = Experience.entries.map { it to it.label },
                        selected = experience, onSelect = { experience = it }, accent = accent
                    )
                    2 -> SingleSelectStep(
                        options = EquipmentKind.entries.map { it to it.label },
                        selected = equipment, onSelect = { equipment = it }, accent = accent
                    )
                    3 -> SingleSelectStep(
                        options = listOf(30, 45, 60, 90).map { it to "$it МИНУТ" },
                        selected = timePerSession, onSelect = { timePerSession = it }, accent = accent
                    )
                    4 -> {
                        Text(
                            "ОТМЕТЬ ВСЁ ЧТО ЕСТЬ",
                            style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        MultiSelectStep(
                            options = Injury.entries.map { it to it.label },
                            selected = injuries,
                            onToggle = { v ->
                                injuries = if (v == Injury.NONE) {
                                    if (injuries.contains(Injury.NONE)) emptySet() else setOf(Injury.NONE)
                                } else {
                                    val w = injuries - Injury.NONE
                                    if (w.contains(v)) w - v else w + v
                                }
                            },
                            accent = accent
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                SciFiPrimaryButton(
                    text = if (isLast) "К ВЫБОРУ ГЕРОЯ →" else "ПРОДОЛЖИТЬ →",
                    accent = accent,
                    onClick = next,
                    enabled = canProceed
                )
            }
        }
    }
}

/* ---- Step 0 : Basic data (sex + age + height + weight + BMI analysis) ---- */

@Composable
private fun BasicDataStep(
    sex: Gender?, age: String, height: String, weight: String,
    onSex: (Gender) -> Unit, onAge: (String) -> Unit,
    onHeight: (String) -> Unit, onWeight: (String) -> Unit,
    accent: Color
) {
    Column {
        // Section label
        Text(
            text = "ПОЛ // SEX",
            style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            SciFiCard(
                selected = sex == Gender.MALE,
                accent = accent,
                onClick = { onSex(Gender.MALE) },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "МУЖСКОЙ",
                    style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 2.sp, color = if (sex == Gender.MALE) accent else HeroPalette.Neutral400)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "MALE",
                    style = TextStyle(fontFamily = Orbitron, fontSize = 8.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral600)
                )
            }
            SciFiCard(
                selected = sex == Gender.FEMALE,
                accent = accent,
                onClick = { onSex(Gender.FEMALE) },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "ЖЕНСКИЙ",
                    style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 2.sp, color = if (sex == Gender.FEMALE) accent else HeroPalette.Neutral400)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "FEMALE",
                    style = TextStyle(fontFamily = Orbitron, fontSize = 8.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral600)
                )
            }
        }
        Spacer(Modifier.height(14.dp))

        SciFiNumericField(
            label = "ВОЗРАСТ", subLabel = "AGE // YEARS",
            value = age, onChange = onAge, accent = accent, suffix = "лет"
        )
        Spacer(Modifier.height(10.dp))
        SciFiNumericField(
            label = "РОСТ", subLabel = "HEIGHT // CM",
            value = height, onChange = onHeight, accent = accent, suffix = "см"
        )
        Spacer(Modifier.height(10.dp))
        SciFiNumericField(
            label = "ВЕС", subLabel = "WEIGHT // KG",
            value = weight, onChange = onWeight, accent = accent, suffix = "кг"
        )

        val bmi = calcBmi(weight.toIntOrNull(), height.toIntOrNull())
        if (bmi != null) {
            Spacer(Modifier.height(18.dp))
            SystemAnalysisPanel(
                bmi = bmi.value,
                bmiCategory = bmi.category.label,
                categoryColor = bmi.category.color,
                idealMin = bmi.idealMin,
                idealMax = bmi.idealMax,
                recommendation = adviceFor(bmi.category)
            )
        }
    }
}

private fun adviceFor(c: BmiCategory): String = when (c) {
    BmiCategory.UNDERWEIGHT -> "Акцент на питание, силовой набор массы"
    BmiCategory.NORMAL -> "База готова. Работаем на выносливость и рельеф"
    BmiCategory.OVERWEIGHT -> "Кардио + дефицит калорий ~10%"
    BmiCategory.OBESE_1 -> "Низкоударные тренировки, дефицит калорий ~15%"
    BmiCategory.OBESE_2 -> "Ходьба / бассейн. Рекомендована консультация врача"
    BmiCategory.OBESE_3 -> "Программа требует медицинского сопровождения"
}

/* ---- Generic steps ---- */

@Composable
private fun <T> SingleSelectStep(
    options: List<Pair<T, String>>, selected: T?,
    onSelect: (T) -> Unit, accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (v, label) ->
            SciFiCard(
                selected = selected == v,
                accent = accent,
                onClick = { onSelect(v) },
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp,
                        color = if (selected == v) accent else HeroPalette.Neutral300
                    )
                )
            }
        }
    }
}

@Composable
private fun <T> MultiSelectStep(
    options: List<Pair<T, String>>, selected: Set<T>,
    onToggle: (T) -> Unit, accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (v, label) ->
            SciFiCard(
                selected = selected.contains(v),
                accent = accent,
                onClick = { onToggle(v) },
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp,
                        color = if (selected.contains(v)) accent else HeroPalette.Neutral300
                    )
                )
            }
        }
    }
}
