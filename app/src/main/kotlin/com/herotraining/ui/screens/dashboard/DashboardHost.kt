package com.herotraining.ui.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/** Local sub-view within the dashboard (not a separate nav route). */
private enum class View { MAIN, TRAINING, NUTRITION, BONUS, GEAR }

/**
 * Container that owns DashboardViewModel and swaps between the main dashboard
 * and one of the four detail views.
 */
@Composable
fun DashboardHost(onReset: () -> Unit, onProfile: () -> Unit) {
    val vm: DashboardViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    var view by remember { mutableStateOf(View.MAIN) }

    when (view) {
        View.MAIN -> DashboardScreen(
            onTraining = { view = View.TRAINING },
            onNutrition = { view = View.NUTRITION },
            onBonus = { view = View.BONUS },
            onGear = { view = View.GEAR },
            onReset = onReset,
            onProfile = onProfile,
            vm = vm
        )
        View.TRAINING -> TrainingViewScreen(
            state = state,
            onBack = { view = View.MAIN },
            onComplete = { vm.markTraining() }
        )
        View.NUTRITION -> NutritionViewScreen(
            state = state,
            onBack = { view = View.MAIN },
            onComplete = { vm.markNutrition() },
            onAddLibraryItem = { vm.addLibraryMeal(it) },
            onAddCustom = { text, p -> vm.addCustomMeal(text, p) },
            onRemoveMeal = { vm.removeMeal(it) }
        )
        View.BONUS -> BonusViewScreen(
            state = state,
            onBack = { view = View.MAIN },
            onComplete = { vm.markBonus() }
        )
        View.GEAR -> GearViewScreen(
            state = state,
            onBack = { view = View.MAIN },
            onSave = { vm.updateGear(it) }
        )
    }
}
