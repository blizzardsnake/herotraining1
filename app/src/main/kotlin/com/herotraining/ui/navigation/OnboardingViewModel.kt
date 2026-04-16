package com.herotraining.ui.navigation

import androidx.lifecycle.ViewModel
import com.herotraining.data.model.Baseline
import com.herotraining.data.model.HeroBuild
import com.herotraining.data.model.NutritionProfile
import com.herotraining.data.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds in-progress onboarding data across the form screens (Profile → Gear → Build → Nutrition → Baseline → Summary).
 * Persisted once user hits the final "start" button in OnboardingSummaryScreen.
 */
class OnboardingViewModel : ViewModel() {
    private val _draft = MutableStateFlow(OnboardingDraft())
    val draft: StateFlow<OnboardingDraft> = _draft.asStateFlow()

    fun setProfile(profile: Profile) {
        _draft.value = _draft.value.copy(profile = profile)
    }

    fun setGear(gear: Set<String>) {
        _draft.value = _draft.value.copy(gear = gear)
    }

    fun setBuild(build: HeroBuild) {
        _draft.value = _draft.value.copy(build = build)
    }

    fun setNutrition(nutrition: NutritionProfile) {
        _draft.value = _draft.value.copy(nutrition = nutrition)
    }

    fun setBaseline(baseline: Baseline) {
        _draft.value = _draft.value.copy(baseline = baseline)
    }
}

data class OnboardingDraft(
    val profile: Profile? = null,
    val gear: Set<String> = emptySet(),
    val build: HeroBuild? = null,
    val nutrition: NutritionProfile? = null,
    val baseline: Baseline? = null
)
