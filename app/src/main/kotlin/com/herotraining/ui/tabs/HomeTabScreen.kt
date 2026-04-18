package com.herotraining.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.herotraining.HeroApp
import com.herotraining.domain.schedule.Unlocks
import com.herotraining.ui.screens.dashboard.DashboardHost
import com.herotraining.ui.screens.tomorrow.TomorrowLockScreen
import kotlinx.coroutines.launch

/**
 * ГЛАВНАЯ tab.
 *
 * Два режима:
 *   1) TOMORROW-LOCK — онбординг завершён, но ещё не наступило 10:00 следующего дня.
 *      Показываем countdown + возможность записывать еду (наблюдение паттерна).
 *   2) NORMAL       — обычный Dashboard.
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
    val scope = rememberCoroutineScope()
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
            buildPhilosophy = state.build?.philosophy,
            todayMeals = state.todayMeals,
            onLogMeal = { text, kcal, untracked ->
                scope.launch {
                    app.stateRepository.addMeal(
                        text = text,
                        kcal = kcal,
                        portion = null,
                        untracked = untracked,
                        comboDelta = 0   // никаких наград за логирование в режиме наблюдения
                    )
                }
            }
        )
    } else {
        DashboardHost(
            onReset = onReset,
            onProfile = onOpenProfile
        )
    }
}
