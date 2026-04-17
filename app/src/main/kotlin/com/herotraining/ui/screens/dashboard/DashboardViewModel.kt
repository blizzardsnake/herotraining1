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
    private val hc = (app as HeroApp).healthConnect
    private val updateDownloader = com.herotraining.update.UpdateDownloader(app)

    val state: StateFlow<UserState> = repo.observeState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), DEFAULT_USER_STATE)

    private val _steps = kotlinx.coroutines.flow.MutableStateFlow(0L)
    val steps: StateFlow<Long> = _steps

    private val _update = kotlinx.coroutines.flow.MutableStateFlow<com.herotraining.ui.components.UpdateState>(
        com.herotraining.ui.components.UpdateState.Hidden
    )
    val update: StateFlow<com.herotraining.ui.components.UpdateState> = _update

    init {
        viewModelScope.launch { repo.rolloverDayIfNeeded() }
        viewModelScope.launch { refreshHealth() }
        viewModelScope.launch { checkForUpdate() }
    }

    private suspend fun refreshHealth() {
        if (hc.hasAllPermissions()) {
            _steps.value = hc.todaySteps()
        }
    }

    fun onHealthPermissionsGranted() = viewModelScope.launch { refreshHealth() }

    private suspend fun checkForUpdate() {
        val info = com.herotraining.update.UpdateChecker.check(getApplication()) ?: return
        _update.value = com.herotraining.ui.components.UpdateState.Available(info)
    }

    fun downloadUpdate(info: com.herotraining.update.UpdateInfo) {
        _update.value = com.herotraining.ui.components.UpdateState.Downloading(info, 0)
        updateDownloader.download(
            info = info,
            onProgress = { pct ->
                val cur = _update.value
                if (cur is com.herotraining.ui.components.UpdateState.Downloading) {
                    _update.value = cur.copy(percent = pct)
                }
            },
            onReady = { file ->
                _update.value = com.herotraining.ui.components.UpdateState.ReadyToInstall(info, file.absolutePath)
            }
        )
    }

    fun launchInstaller() {
        val cur = _update.value
        if (cur is com.herotraining.ui.components.UpdateState.ReadyToInstall) {
            updateDownloader.launchInstaller(java.io.File(cur.apkPath))
        }
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
