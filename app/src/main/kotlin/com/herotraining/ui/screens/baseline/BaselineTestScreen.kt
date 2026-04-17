package com.herotraining.ui.screens.baseline

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChecklistRtl
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.catalog.TestExerciseCatalog
import com.herotraining.data.catalog.TestKind
import com.herotraining.data.model.Baseline
import com.herotraining.data.model.Hero
import com.herotraining.data.model.Injury
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.components.SelectButton
import com.herotraining.ui.components.StepProgress
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun BaselineTestScreen(
    hero: Hero,
    injuries: Set<Injury>,
    onBack: () -> Unit,
    onComplete: (Baseline) -> Unit
) {
    val tests = remember(injuries) { TestExerciseCatalog.forInjuries(injuries) }
    // step=-1 — intro; 0..tests.size-1 — current exercise
    var step by remember { mutableStateOf(-1) }
    val results = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var cur by remember { mutableStateOf("") }
    var scaleValue by remember { mutableStateOf<Int?>(null) }

    if (step == -1) {
        Intro(hero, tests.map { it.name }, onBack) { step = 0 }
        return
    }

    val test = tests[step]
    val isLast = step == tests.lastIndex

    val canProceed = when (test.kind) {
        TestKind.SCALE -> scaleValue != null
        else -> cur.toIntOrNull() != null
    }

    val saveAndNext = {
        val value = if (test.kind == TestKind.SCALE) (scaleValue ?: 0) else (cur.toIntOrNull() ?: 0)
        results.value = results.value + (test.id to value)
        cur = ""; scaleValue = null
        if (isLast) onComplete(buildBaseline(results.value))
        else step += 1
    }
    val skip = {
        results.value = results.value + (test.id to 0)
        cur = ""; scaleValue = null
        if (isLast) onComplete(buildBaseline(results.value))
        else step += 1
    }
    val prev = {
        if (step == 0) { step = -1 } else { step -= 1; cur = ""; scaleValue = null }
    }

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
                text = "← НАЗАД",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { prev() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            StepProgress(current = step, total = tests.size, activeColor = hero.color)
            Spacer(Modifier.height(20.dp))
            Text(
                text = "УПРАЖНЕНИЕ ${step + 1}/${tests.size}",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = test.name.uppercase(),
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = hero.color
                )
            )
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HeroPalette.Neutral800)
                    .padding(14.dp)
            ) {
                Text(
                    text = test.description,
                    style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300)
                )
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.width(2.dp).height(20.dp).background(hero.color))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = test.instruction,
                        style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            when (test.kind) {
                TestKind.SCALE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        test.scaleOptions.forEach { (v, label) ->
                            SelectButton(
                                label = "$v. $label",
                                selected = scaleValue == v,
                                accentColor = hero.color,
                                onClick = { scaleValue = v }
                            )
                        }
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = cur,
                            onValueChange = { cur = it.filter(Char::isDigit).take(4) },
                            placeholder = { Text("0", color = HeroPalette.Neutral700) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontFamily = ImpactLike, fontSize = 32.sp,
                                fontWeight = FontWeight.Black, color = hero.color,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = hero.color,
                                unfocusedIndicatorColor = hero.color.copy(alpha = 0.5f),
                                cursorColor = hero.color
                            )
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = test.unit,
                            style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, HeroPalette.Neutral800)
                        .clickable(onClick = skip)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "НЕ МОГУ",
                        style = TextStyle(fontSize = 11.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
                    )
                }
                PrimaryOutlinedButton(
                    text = if (isLast) "ЗАВЕРШИТЬ →" else "ДАЛЕЕ →",
                    accentColor = hero.color,
                    onClick = saveAndNext,
                    enabled = canProceed,
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}

@Composable
private fun Intro(hero: Hero, names: List<String>, onBack: () -> Unit, onStart: () -> Unit) {
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
                text = "← НАЗАД",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ChecklistRtl, contentDescription = null, tint = hero.color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ТЕСТ БАЗЫ",
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 28.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
            }
            Text(
                text = "ПОСЛЕДНИЙ ШАГ",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, hero.color)
                    .padding(16.dp)
            ) {
                Text(
                    text = "${names.size} упражнений.",
                    style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300)
                )
            }
            Spacer(Modifier.height(12.dp))
            names.forEachIndexed { idx, name ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (idx + 1).toString().padStart(2, '0'),
                        style = TextStyle(fontSize = 10.sp, color = HeroPalette.Neutral600),
                        modifier = Modifier.width(24.dp)
                    )
                    Icon(Icons.Filled.Check, contentDescription = null, tint = HeroPalette.Neutral500, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = name, style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300))
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral900))
            }
            Spacer(Modifier.height(20.dp))
            PrimaryOutlinedButton(
                text = "НАЧАТЬ →",
                accentColor = hero.color,
                onClick = onStart
            )
        }
    }
}

private fun buildBaseline(map: Map<String, Int>): Baseline = Baseline(
    pushups = map["pushups"] ?: 0,
    squats = map["squats"] ?: 0,
    plankSec = map["plank"] ?: 0,
    pullups = map["pullups"] ?: 0,
    burpees = map["burpees"] ?: 0,
    cardioMinutes = map["cardio"] ?: 0,
    flexibilityScale = map["flexibility"] ?: 0
)
