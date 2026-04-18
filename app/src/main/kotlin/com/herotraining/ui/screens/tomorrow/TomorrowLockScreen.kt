package com.herotraining.ui.screens.tomorrow

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.LoggedMeal
import com.herotraining.domain.schedule.Unlocks
import com.herotraining.ui.components.HeroBackgroundScaffold
import com.herotraining.ui.components.MealKind
import com.herotraining.ui.components.MealLogDialog
import com.herotraining.ui.scifi.CornerBrackets
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani
import com.herotraining.ui.theme.heroTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Экран-замок между онбордингом и 10:00 следующего дня.
 *
 * Пока юзер ждёт первой миссии — МОЖЕТ:
 *   - Смотреть countdown
 *   - Записывать приёмы пищи (наблюдение паттерна: когда в реале ест)
 *   - Ходить в другие табы (профиль/прогресс/квесты)
 *
 * НЕ МОЖЕТ:
 *   - Запускать тренировку (она и UI для неё появятся после 10:00)
 *
 * Сегодняшние логи питания показываются списком под countdown'ом — юзер видит что "да,
 * приложение меня слушает" и механика честности запускается с первого дня.
 */
@Composable
fun TomorrowLockScreen(
    programStartEpochMs: Long,
    heroName: String?,
    buildPhilosophy: String?,
    todayMeals: List<LoggedMeal>,
    onLogMeal: (text: String, kcal: Int, untracked: Boolean) -> Unit
) {
    val th = heroTheme()
    val accent = th.heroColor

    val trainingUnlockMs = Unlocks.trainingUnlockAt(programStartEpochMs)
    val nutritionUnlockMs = Unlocks.nutritionUnlockAt(programStartEpochMs)

    var nowMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(trainingUnlockMs) {
        while (true) {
            nowMs = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val remainingTraining = (trainingUnlockMs - nowMs).coerceAtLeast(0L)
    val remainingNutrition = (nutritionUnlockMs - nowMs).coerceAtLeast(0L)

    var showMealDialog by remember { mutableStateOf(false) }

    HeroBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header pill
            Row(
                modifier = Modifier.border(1.dp, accent).padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.LockClock, null, tint = accent, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "ПРОТОКОЛ ЗАГРУЖЕН · ОЖИДАНИЕ",
                    style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent)
                )
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = heroName?.let { "ЗАВТРА ТЫ ВСТУПАЕШЬ В РИТМ ${it.uppercase()}" }
                    ?: "ЗАВТРА НАЧИНАЕТСЯ ПРОТОКОЛ",
                style = TextStyle(
                    fontFamily = Rajdhani, fontWeight = FontWeight.Bold,
                    fontSize = 28.sp, color = Color.White, letterSpacing = 1.sp, lineHeight = 32.sp
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Все мы любим начинать завтра. Мы это уважаем.",
                style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400, lineHeight = 14.sp)
            )

            Spacer(Modifier.height(24.dp))

            // Countdown card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, accent)
                    .background(accent.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 20.dp)
            ) {
                CornerBrackets(color = accent, armLength = 18.dp, thickness = 2.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "ПЕРВАЯ МИССИЯ ЧЕРЕЗ",
                        style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral400)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        formatCountdown(remainingTraining),
                        style = TextStyle(
                            fontFamily = ImpactLike, fontWeight = FontWeight.Black,
                            fontSize = 48.sp, color = accent, letterSpacing = 2.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "РАЗБЛОК В ${formatClockTime(trainingUnlockMs)}",
                        style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Nutrition unlock info
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(14.dp)
            ) {
                Text(
                    "ПЛАН ПИТАНИЯ · ДЕНЬ 1",
                    style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (remainingNutrition > 0)
                        "Откроется в ${formatClockTime(nutritionUnlockMs)} — через ${formatCountdown(remainingNutrition)}."
                    else "✓ УЖЕ ДОСТУПЕН",
                    style = TextStyle(fontFamily = Rajdhani, fontSize = 14.sp, color = HeroPalette.Neutral300)
                )
            }

            Spacer(Modifier.height(18.dp))

            // ── MEAL LOG — доступен прямо сейчас, даже до unlock'а
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, accent.copy(alpha = 0.5f)).padding(14.dp)
            ) {
                Text(
                    "ПИТАНИЕ · НАБЛЮДЕНИЕ",
                    style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Первую неделю просто записывай что ешь. Без подсчётов, без планов — просто честно отмечай. " +
                        "Мы учимся твоему ритму чтобы со 2-й недели построить план под тебя.",
                    style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400, lineHeight = 15.sp)
                )
                Spacer(Modifier.height(10.dp))

                Text(
                    "📝 ЗАПИСАТЬ ПРИЁМ ПИЩИ",
                    style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, accent)
                        .clickable { showMealDialog = true }
                        .padding(vertical = 10.dp),
                )

                if (todayMeals.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "СЕГОДНЯ ЗАПИСАНО",
                        style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                    )
                    Spacer(Modifier.height(4.dp))
                    todayMeals.forEach { m ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Check, null, tint = accent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    m.text,
                                    style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300),
                                    maxLines = 2
                                )
                                val kcalText = if (m.kcal > 0) "${m.kcal} ккал · ${m.time}"
                                               else "не подсчитано · ${m.time}"
                                Text(
                                    kcalText,
                                    style = TextStyle(fontSize = 10.sp, color = HeroPalette.Neutral600)
                                )
                            }
                        }
                        Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral900))
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            if (!buildPhilosophy.isNullOrBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral900).padding(14.dp)
                ) {
                    Text(
                        "«$buildPhilosophy»",
                        style = TextStyle(
                            fontFamily = Rajdhani, fontSize = 14.sp,
                            color = HeroPalette.Neutral300, lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }

    if (showMealDialog) {
        MealLogDialog(
            accent = accent,
            onDismiss = { showMealDialog = false },
            onSave = { text, kcal, untracked ->
                onLogMeal(text, kcal, untracked)
                showMealDialog = false
            },
            defaultKind = guessMealKind(nowMs)
        )
    }
}

/** Угадываем тип приёма по текущему часу — чтоб диалог открывался с адекватным default'ом. */
private fun guessMealKind(nowMs: Long): MealKind {
    val hour = Calendar.getInstance().apply { timeInMillis = nowMs }.get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..10 -> MealKind.BREAKFAST
        in 11..15 -> MealKind.LUNCH
        in 16..21 -> MealKind.DINNER
        else -> MealKind.SNACK
    }
}

private fun formatCountdown(ms: Long): String {
    val s = ms / 1000
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h >= 1) String.format(Locale.ROOT, "%02d:%02d:%02d", h, m, sec)
           else String.format(Locale.ROOT, "%02d:%02d", m, sec)
}

private fun formatClockTime(epochMs: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(epochMs))
