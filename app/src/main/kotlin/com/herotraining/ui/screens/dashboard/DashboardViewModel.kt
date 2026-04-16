package com.herotraining.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.herotraining.HeroApp
import com.herotraining.data.model.DEFAULT_USER_STATE
import com.herotraining.data.model.UserState
import com.herotraining.data.repo.QuestType
import com.herotraining.data.repo.StateRepository
import com.herotraining.domain.calc.getComboBonus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: StateRepository = (app as HeroApp).stateRepository

    val state: StateFlow<UserState> = repo.observeState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), DEFAULT_USER_STATE)

    init {
        viewModelScope.launch { repo.rolloverDayIfNeeded() }
    }

    fun markTraining() = mark(QuestType.TRAINING, rp = 15, comboDelta = 15)
    fun markNutrition() = mark(QuestType.NUTRITION, rp = 10, comboDelta = 10)
    fun markBonus() = mark(QuestType.BONUS, rp = 5, comboDelta = 8)

    private fun mark(type: QuestType, rp: Int, comboDelta: Int) {
        viewModelScope.launch {
            val combo = state.value.combo
            val mult = getComboBonus(combo).xpMult
            repo.markQuest(type, rp, comboDelta, mult)
        }
    }

    fun updateGear(ids: Set<String>) = viewModelScope.launch { repo.setGear(ids) }
    fun reset() = viewModelScope.launch { repo.reset() }

    fun addLibraryMeal(item: com.herotraining.data.model.FoodItem) = viewModelScope.launch {
        repo.addMeal(text = item.name, kcal = item.kcal, portion = null, untracked = false, comboDelta = 5)
    }

    fun addCustomMeal(text: String, portion: com.herotraining.data.catalog.PortionSize) = viewModelScope.launch {
        repo.addMeal(
            text = text,
            kcal = portion.kcal,
            portion = portion.id,
            untracked = portion.untracked,
            comboDelta = if (portion.untracked) 2 else 5
        )
    }

    fun removeMeal(meal: com.herotraining.data.model.LoggedMeal) = viewModelScope.launch {
        repo.deleteMeal(meal)
    }
}
