package com.herotraining.ui.tabs.workouts

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.HeroApp
import com.herotraining.data.catalog.exercises.GearKind
import com.herotraining.domain.program.ExerciseBlock
import com.herotraining.domain.program.WorkoutDay
import com.herotraining.domain.program.WorkoutGenerator
import com.herotraining.domain.schedule.Unlocks
import com.herotraining.ui.components.HeroBackgroundScaffold
import com.herotraining.ui.scifi.CornerBrackets
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani
import com.herotraining.ui.theme.heroTheme
import kotlinx.coroutines.launch

/**
 * Вкладка ТРЕНИРОВКИ. Три состояния:
 *   1) LOCKED    — ещё не 10:00 первого дня. Показываем заглушку с countdown'ом.
 *   2) READY     — тренировка сгенерирована, юзер её ещё не выполнил.
 *                  Показываем список упражнений с чек-боксами, "ТРЕНИРОВКА ЗАВЕРШЕНА" в конце.
 *   3) COMPLETED — state.todayTrainingDone=true. Показываем "✓ на сегодня всё" + цитату.
 */
@Composable
fun WorkoutsTabScreen(onBackToHome: () -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val app = ctx.applicationContext as HeroApp
    val scope = rememberCoroutineScope()
    val state by app.stateRepository.observeState()
        .collectAsState(initial = com.herotraining.data.model.DEFAULT_USER_STATE)

    val programStart = state.programStartEpochMs
    val trainingLocked = programStart != null &&
        state.onboarded &&
        Unlocks.isBeforeFirstTraining(programStart)

    when {
        trainingLocked -> LockedView(
            programStartMs = programStart!!
        )
        state.todayTrainingDone -> CompletedView(
            philosophy = state.build?.philosophy,
            onBack = onBackToHome
        )
        else -> {
            val workout = remember(state) { WorkoutGenerator.forToday(state) }
            if (workout == null) {
                NoDataView(onBack = onBackToHome)
            } else {
                ActiveView(
                    workout = workout,
                    onComplete = {
                        scope.launch {
                            app.stateRepository.markQuest(
                                type = com.herotraining.data.repo.QuestType.TRAINING,
                                rpBase = 10,
                                comboDelta = 15,
                                comboMultiplier = 1.2
                            )
                        }
                    }
                )
            }
        }
    }
}

/* ----------------------------------------------------------------
   Locked (до 10:00 первого дня)
---------------------------------------------------------------- */
@Composable
private fun LockedView(programStartMs: Long) {
    val th = heroTheme()
    val accent = th.heroColor
    val unlockAt = Unlocks.trainingUnlockAt(programStartMs)

    HeroBackgroundScaffold {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                CornerBrackets(color = accent, armLength = 18.dp, thickness = 2.dp)
                Icon(Icons.Filled.LockClock, null, tint = accent, modifier = Modifier.size(48.dp).padding(10.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "ТРЕНИРОВКА ЖДЁТ",
                style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 26.sp, color = Color.White, letterSpacing = 1.sp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Разблок в ${formatTime(unlockAt)} — смотри countdown на ГЛАВНОЙ.",
                style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral400),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

/* ----------------------------------------------------------------
   Completed (сегодня уже сделано)
---------------------------------------------------------------- */
@Composable
private fun CompletedView(philosophy: String?, onBack: () -> Unit) {
    val th = heroTheme()
    val accent = th.heroColor
    HeroBackgroundScaffold {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.CheckCircle, null, tint = accent, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(14.dp))
            Text(
                "ТРЕНИРОВКА ВЫПОЛНЕНА",
                style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = accent, letterSpacing = 1.sp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Следующая миссия — завтра в 10:00.",
                style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral400)
            )
            if (!philosophy.isNullOrBlank()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "«$philosophy»",
                    style = TextStyle(fontFamily = Rajdhani, fontSize = 14.sp, color = HeroPalette.Neutral300, fontStyle = FontStyle.Italic, lineHeight = 20.sp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

/* ----------------------------------------------------------------
   NoData (нет билда/профиля — страховка)
---------------------------------------------------------------- */
@Composable
private fun NoDataView(onBack: () -> Unit) {
    HeroBackgroundScaffold {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Не могу собрать тренировку — не хватает данных профиля.",
                style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral400)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "НА ГЛАВНУЮ →",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onBack() }.padding(10.dp)
            )
        }
    }
}

/* ----------------------------------------------------------------
   Active — сгенерированная тренировка, чек-лист, финальная кнопка
---------------------------------------------------------------- */
@Composable
private fun ActiveView(workout: WorkoutDay, onComplete: () -> Unit) {
    val th = heroTheme()
    val accent = th.heroColor

    // Локальный чек-лист — для v0.9.0 не сохраняем в Room (это v0.9.1 — per-set tracking).
    // Каждый блок можно нажать "галкой" чтоб отметить выполнение.
    val done = remember { mutableStateOf(setOf<String>()) }
    val allDone = done.value.size == workout.blocks.size && workout.blocks.isNotEmpty()

    HeroBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.FitnessCenter, null, tint = accent, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    workout.subtitle,
                    style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold, color = accent)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                workout.title,
                style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White, letterSpacing = 1.sp, lineHeight = 32.sp)
            )
            Spacer(Modifier.height(8.dp))
            Row {
                InfoChip("≈${workout.estimatedMinutes} МИН", accent)
                Spacer(Modifier.width(8.dp))
                InfoChip("${workout.blocks.size} БЛОКОВ", accent)
            }
            Spacer(Modifier.height(18.dp))

            // Блоки упражнений
            workout.blocks.forEachIndexed { idx, block ->
                BlockCard(
                    idx = idx + 1,
                    block = block,
                    accent = accent,
                    isDone = block.exercise.id in done.value,
                    onToggle = {
                        done.value = if (block.exercise.id in done.value)
                            done.value - block.exercise.id
                        else
                            done.value + block.exercise.id
                    }
                )
                Spacer(Modifier.height(10.dp))
            }

            if (!workout.mantra.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "«${workout.mantra}»",
                    style = TextStyle(fontFamily = Rajdhani, fontSize = 13.sp, color = HeroPalette.Neutral400, fontStyle = FontStyle.Italic, lineHeight = 18.sp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(18.dp))

            // Complete button
            Text(
                text = if (allDone) "✓ ЗАВЕРШИТЬ ТРЕНИРОВКУ"
                       else "СНАЧАЛА ОТМЕТЬ ВСЕ УПРАЖНЕНИЯ (${done.value.size}/${workout.blocks.size})",
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 2.sp,
                    color = if (allDone) accent else HeroPalette.Neutral500
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, if (allDone) accent else HeroPalette.Neutral700)
                    .then(if (allDone) Modifier.clickable(onClick = onComplete) else Modifier)
                    .padding(vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun InfoChip(text: String, accent: Color) {
    Box(
        modifier = Modifier.border(1.dp, accent.copy(alpha = 0.6f)).padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 2.sp, color = accent))
    }
}

@Composable
private fun BlockCard(
    idx: Int,
    block: ExerciseBlock,
    accent: Color,
    isDone: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDone) accent else HeroPalette.Neutral800)
            .background(if (isDone) accent.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(onClick = onToggle)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Index / check
            Box(
                modifier = Modifier.size(28.dp).border(1.dp, if (isDone) accent else HeroPalette.Neutral700),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) Icon(Icons.Filled.Check, null, tint = accent, modifier = Modifier.size(16.dp))
                else Text("$idx", style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral500))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    block.exercise.nameRu,
                    style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                )
                Text(
                    block.exercise.primary.label +
                        if (block.exercise.secondary.isNotEmpty())
                            " · " + block.exercise.secondary.joinToString(", ") { it.label } else "",
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral500)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${block.sets}×${block.prescription}",
                    style = TextStyle(fontFamily = ImpactLike, fontWeight = FontWeight.Black, fontSize = 18.sp, color = accent)
                )
                Text(
                    "ОТДЫХ ${block.restSec}с",
                    style = TextStyle(fontSize = 9.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral600)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            block.exercise.instructionRu,
            style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral400, lineHeight = 16.sp)
        )
        // Gear chip
        Spacer(Modifier.height(6.dp))
        Row {
            block.exercise.gearAny.forEach { gear ->
                Box(
                    modifier = Modifier.border(1.dp, HeroPalette.Neutral800).padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(gear.label, style = TextStyle(fontSize = 9.sp, color = HeroPalette.Neutral500))
                }
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

private fun formatTime(epochMs: Long): String =
    java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(epochMs))
