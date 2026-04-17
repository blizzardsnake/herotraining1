package com.herotraining.ui.screens.boot

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import kotlinx.coroutines.delay

/** One boot line. [kind] controls colour; [prefix] is the left-rail marker. */
private data class BootLine(
    val prefix: String,
    val text: String,
    val kind: Kind = Kind.INFO
) {
    enum class Kind { INFO, OK, WARN, CRIT }
}

private val BOOT_SEQUENCE = listOf(
    BootLine("[ ЗАПУСК ]", "Инициализация протокола героя...", BootLine.Kind.INFO),
    BootLine("[ СИСТ.  ]", "Проверка целостности ядра — ОК", BootLine.Kind.OK),
    BootLine("[ АУТ.   ]", "Распаковка архива воина...", BootLine.Kind.INFO),
    BootLine("[ ПАМЯТЬ ]", "Загрузка дисциплины", BootLine.Kind.INFO),
    BootLine("[ ЯДРО   ]", "Подготовка тела", BootLine.Kind.INFO),
    BootLine("[ СИНХР. ]", "Связь с датчиками здоровья", BootLine.Kind.INFO),
    BootLine("[ ДУХ    ]", "Калибровка ярости — 87%", BootLine.Kind.OK),
    BootLine("[ ВНИМ.  ]", "Жалость отключена", BootLine.Kind.WARN),
    BootLine("[ ВНИМ.  ]", "Оправдания заблокированы", BootLine.Kind.WARN),
    BootLine("[   >   ]", "АКТИВАЦИЯ РЕЖИМА: БЕЗ ОТКАТА", BootLine.Kind.CRIT),
    BootLine("[ ГОТОВ  ]", "Протокол готов. Добро пожаловать, воин.", BootLine.Kind.OK),
)

/**
 * Terminal-style boot splash. Prints lines one after another, then calls [onReady].
 */
@Composable
fun BootSplashScreen(onReady: () -> Unit) {
    val printed = remember { mutableStateListOf<BootLine>() }
    var blinkCaret by remember { mutableStateOf(true) }

    // Caret blink
    LaunchedEffect(Unit) {
        while (true) { blinkCaret = !blinkCaret; delay(420) }
    }

    // Line-by-line sequence
    LaunchedEffect(Unit) {
        delay(250) // initial pause
        for (line in BOOT_SEQUENCE) {
            printed.add(line)
            delay(280)
        }
        delay(650)
        onReady()
    }

    val progress: Float by animateFloatAsState(
        targetValue = printed.size.toFloat() / BOOT_SEQUENCE.size.toFloat(),
        animationSpec = tween(durationMillis = 250, easing = LinearEasing),
        label = "bootProgress"
    )

    val ctx = LocalContext.current
    val versionName = remember {
        runCatching { ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName }.getOrNull() ?: "?"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 40.dp)
        ) {
            // Header badge
            Box(
                modifier = Modifier
                    .border(1.dp, HeroPalette.Red500)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ПРОТОКОЛ ГЕРОЯ",
                    style = TextStyle(
                        color = HeroPalette.Red500,
                        fontSize = 10.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Spacer(Modifier.height(20.dp))

            Text(
                text = "ЗАПУСК СИСТЕМЫ v$versionName",
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = Color.White
                )
            )
            Spacer(Modifier.height(24.dp))

            // Console area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, HeroPalette.Neutral800)
                    .background(Color(0xFF050505))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                printed.forEach { line -> BootLineRow(line) }
                if (printed.size < BOOT_SEQUENCE.size) {
                    Text(
                        text = if (blinkCaret) "█" else " ",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            color = HeroPalette.Red500,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ЗАГРУЗКА",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = HeroPalette.Neutral500
                    )
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(HeroPalette.Neutral900)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(6.dp)
                            .background(HeroPalette.Red500)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${(progress * 100).toInt().toString().padStart(2, '0')}%",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = HeroPalette.Red500
                    )
                )
            }
        }
    }
}

@Composable
private fun BootLineRow(line: BootLine) {
    val color = when (line.kind) {
        BootLine.Kind.INFO -> HeroPalette.Neutral400
        BootLine.Kind.OK -> Color(0xFF10B981)
        BootLine.Kind.WARN -> Color(0xFFE6B800)
        BootLine.Kind.CRIT -> HeroPalette.Red500
    }
    val weight = if (line.kind == BootLine.Kind.CRIT) FontWeight.Black else FontWeight.Normal
    Row {
        Text(
            text = line.prefix,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = color.copy(alpha = 0.7f)
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = line.text,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = color,
                fontWeight = weight
            )
        )
    }
}
