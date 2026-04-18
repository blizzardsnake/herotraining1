package com.herotraining.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.herotraining.HeroApp
import com.herotraining.domain.schedule.Unlocks
import com.herotraining.ui.screens.dashboard.DashboardHost
import com.herotraining.ui.screens.tomorrow.TomorrowLockScreen

/**
 * ГЛАВНАЯ tab.
 *
 * Two modes:
 *   1) TOMORROW-LOCK — если онбординг завершён, но ещё не наступило 10:00 следующего дня,
 *      показываем [TomorrowLockScreen] с countdown. Тренировка заблокирована до анлока.
 *   2) NORMAL       — обычный Dashboard/Home.
 *
 * Nav-кнопки внизу всё ещё активны: юзер может уйти в Профиль/КВЕСТЫ/ПРОГРЕСС/ТРЕНИРОВКИ
 * (хотя последняя тоже заблочится в v0.8).
 */
@Composable
fun HomeTabScreen(
    onReset: () -> Unit,
    onOpenWorkouts: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val app = ctx.applicationContext as HeroApp
    val state by app.stateRepository.observeState()
        .collectAsState(initial = com.herotraining.data.model.DEFAULT_USER_STATE)

    val programStart = state.programStartEpochMs
    val locked = programStart != null &&
        state.onboarded &&
        Unlocks.isBeforeFirstTraining(programStart)

    if (locked) {
        TomorrowLockScreen(
            programStartEpochMs = programStart!!,
            heroName = state.hero?.name,
            buildPhilosophy = state.build?.philosophy
        )
    } else {
        DashboardHost(
            onReset = onReset,
            onProfile = onOpenProfile
        )
    }
}
