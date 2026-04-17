package com.herotraining.ui.screens.disclaimer

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.components.HeroBackgroundScaffold
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.scifi.CornerBrackets
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani

/**
 * First personal-data screen. User MUST:
 *   1) Scroll to the bottom (proves they saw it)
 *   2) Tick the checkbox
 *   3) Hit "Погнали" to continue
 *
 * No sneaky auto-dismiss. The app cannot ask for age/weight/food prefs until this
 * explicit acknowledgement lands. State persists via StateRepository.acceptDisclaimer().
 */
@Composable
fun DisclaimerScreen(
    onAccepted: () -> Unit
) {
    var checked by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val accent = HeroPalette.Red500

    // Track whether the user has scrolled far enough to see the whole text.
    // Allows the CTA to engage only after scroll-to-bottom + checkbox.
    val scrolledToEnd = scrollState.value >= scrollState.maxValue - 20

    HeroBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp, bottom = 24.dp)
                .widthIn(max = 640.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // ── Header ────────────────────────────────────────────
            Text(
                text = "ПРОТОКОЛ / ПРАВИЛА",
                style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, color = accent, fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "ЧТО ТЫ ВКЛЮЧАЕШЬ",
                style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = Color.White, letterSpacing = 1.sp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "прочти до конца · поставь галочку · погнали",
                style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(18.dp))

            // ── Scrollable body ───────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Section(
                    title = "ЗАЧЕМ МЫ СОБИРАЕМ ДАННЫЕ",
                    accent = accent,
                    body = "Мы трекаем твой сон, приёмы пищи, тренировки и замеры. " +
                            "Первую неделю приложение ПРОСТО НАБЛЮДАЕТ — учится твоему ритму, " +
                            "паттернам питания, времени пробуждения и любимым часам для тренировок. " +
                            "Никаких шаблонов «для всех» — только твой план."
                )
                Section(
                    title = "ЧТО БУДЕТ СО 2-Й НЕДЕЛИ",
                    accent = accent,
                    body = "Как только паттерны ясны — гайки затягиваются ПОСТЕПЕННО:\n" +
                            "• Пропустил запись еды → напомним\n" +
                            "• Спишь в 02:00, а тренировка в 09:00 → подсветим рассинхрон\n" +
                            "• Халтуришь 3 дня подряд → ментор скажет прямо в характере\n" +
                            "Это не спам, а часть механики. Если не зайдёт — отключишь отдельно."
                )
                Section(
                    title = "ПОЧЕМУ ПУШИ",
                    accent = accent,
                    body = "Они нужны чтоб записывать еду вовремя, не забывать про замеры и возвращаться " +
                            "в ритм если выпал. В стиле выбранного героя — Кратос орёт, Леон холодно констатирует, Данте стёбом. " +
                            "Настраивается потом."
                )
                Section(
                    title = "КУДА ИДУТ ДАННЫЕ",
                    accent = accent,
                    body = "• На твоё устройство — в зашифрованную песочницу приложения\n" +
                            "• В твой личный Firebase-аккаунт Google (шифрование at-rest у Google)\n" +
                            "• По сети — только через TLS 1.3\n\n" +
                            "НИКТО КРОМЕ ТЕБЯ данные не видит. Ни разработчики, ни реклама, ни аналитика. " +
                            "Мы не торгуем этим — у нас вообще нет инфраструктуры чтоб кому-то их продавать."
                )
                Section(
                    title = "ЧТО ТЫ ВСЕГДА МОЖЕШЬ",
                    accent = accent,
                    body = "• Полный сброс — всё стирается локально и в облаке, одной кнопкой\n" +
                            "• Выйти из Google — аккаунт отвязывается, данные остаются в облаке до твоего возвращения\n" +
                            "• Отключить пуши на уровне системы Android\n\n" +
                            "Философия простая: дисциплина + честность > любой AI. Приложение учит этим двум вещам. " +
                            "Без них никакой стек не спасёт."
                )

                Spacer(Modifier.height(12.dp))
                if (!scrolledToEnd) {
                    Text(
                        text = "↓ ПРОКРУТИ ДО КОНЦА ↓",
                        style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, color = accent.copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Checkbox row ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(1.dp, if (checked) accent else HeroPalette.Neutral700)
                    .clickable(enabled = scrolledToEnd) { checked = !checked }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(if (checked) accent else Color.Transparent)
                        .border(1.dp, if (checked) accent else HeroPalette.Neutral500),
                    contentAlignment = Alignment.Center
                ) {
                    if (checked) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (scrolledToEnd) "Я ПОНЯЛ. ПОГНАЛИ."
                           else "ДОЧИТАЙ ДО КОНЦА — ПОТОМ ГАЛОЧКА",
                    style = TextStyle(
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp,
                        color = if (scrolledToEnd) Color.White else HeroPalette.Neutral500
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            PrimaryOutlinedButton(
                text = "НАЧАТЬ АНКЕТУ →",
                accentColor = accent,
                onClick = onAccepted,
                enabled = checked && scrolledToEnd
            )
        }
    }
}

@Composable
private fun Section(title: String, body: String, accent: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 12.dp)
                    .background(accent.copy(alpha = 0.8f))
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = accent
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = body,
            style = TextStyle(
                fontFamily = Rajdhani,
                fontSize = 13.sp,
                color = HeroPalette.Neutral300,
                lineHeight = 18.sp
            )
        )
    }
}
