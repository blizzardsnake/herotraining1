package com.herotraining.ui.screens.tomorrow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import com.herotraining.domain.schedule.Unlocks
import com.herotraining.ui.components.HeroBackgroundScaffold
import com.herotraining.ui.scifi.CornerBrackets
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani
import com.herotraining.ui.theme.heroTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экран-замок который висит поверх Dashboard между моментом завершения онбординга
 * и 10:00 следующего календарного дня. Юзер видит:
 *   - Приветственный заголовок (в голосе ментора если есть, иначе общий)
 *   - Countdown "hh:mm:ss" до разблокировки тренировки
 *   - Время когда откроется план питания (06:00 завтра) — просто info
 *   - Цитату из выбранного билда в качестве filler-контента
 *
 * Анкета/профиль/смена героя всё ещё доступны через нижнюю навигацию (мы не забираем
 * у юзера возможность что-то подкрутить пока ждёт).
 */
@Composable
fun TomorrowLockScreen(
    programStartEpochMs: Long,
    heroName: String?,
    buildPhilosophy: String?
) {
    val th = heroTheme()
    val accent = th.heroColor

    val trainingUnlockMs = Unlocks.trainingUnlockAt(programStartEpochMs)
    val nutritionUnlockMs = Unlocks.nutritionUnlockAt(programStartEpochMs)

    // Live countdown — recomposes каждую секунду
    var nowMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(trainingUnlockMs) {
        while (true) {
            nowMs = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val remainingTraining = (trainingUnlockMs - nowMs).coerceAtLeast(0L)
    val remainingNutrition = (nutritionUnlockMs - nowMs).coerceAtLeast(0L)

    HeroBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // ── Header pill ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .border(1.dp, accent)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
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

            // ── Big title ────────────────────────────────────────
            Text(
                text = heroName?.let { "ЗАВТРА ТЫ ВСТУПАЕШЬ В РИТМ ${it.uppercase()}" }
                    ?: "ЗАВТРА НАЧИНАЕТСЯ ПРОТОКОЛ",
                style = TextStyle(
                    fontFamily = Rajdhani, fontWeight = FontWeight.Bold,
                    fontSize = 30.sp, color = Color.White, letterSpacing = 1.sp, lineHeight = 34.sp
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Все мы любим начинать завтра. Мы это уважаем.",
                style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400, lineHeight = 14.sp)
            )

            Spacer(Modifier.height(28.dp))

            // ── Big countdown box ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, accent)
                    .background(accent.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 22.dp)
            ) {
                CornerBrackets(color = accent, armLength = 18.dp, thickness = 2.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "ПЕРВАЯ МИССИЯ ЧЕРЕЗ",
                        style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral400)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = formatCountdown(remainingTraining),
                        style = TextStyle(
                            fontFamily = ImpactLike, fontWeight = FontWeight.Black,
                            fontSize = 56.sp, color = accent, letterSpacing = 2.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "РАЗБЛОК В ${formatClockTime(trainingUnlockMs)}",
                        style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── Nutrition-plan row ───────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HeroPalette.Neutral800)
                    .padding(14.dp)
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
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Подстраивается под стиль героя + твои вкусовые предпочтения.",
                    style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500, lineHeight = 14.sp)
                )
            }

            Spacer(Modifier.height(18.dp))

            // ── Build philosophy filler (if any) ─────────────────
            if (!buildPhilosophy.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HeroPalette.Neutral900)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "«$buildPhilosophy»",
                        style = TextStyle(
                            fontFamily = Rajdhani, fontSize = 14.sp,
                            color = HeroPalette.Neutral300, lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Footer hint ──────────────────────────────────────
            Text(
                text = "Пока висим в режиме ожидания можешь подкрутить профиль или поменять героя через нижнее меню.",
                style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral600, lineHeight = 14.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
