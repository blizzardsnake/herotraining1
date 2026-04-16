package com.herotraining.data.model

/** Runtime UI model — materialized from Room entities. */
data class UserState(
    val onboarded: Boolean,
    val hero: Hero?,
    val build: HeroBuild?,
    val profile: Profile?,
    val nutrition: NutritionProfile?,
    val baseline: Baseline?,
    val gear: Set<String>,

    val programStartEpochMs: Long?,
    val streak: Int,
    val lastCheckin: String?,

    val coins: Int,
    val xp: Int,
    val rankPoints: Int,
    val combo: Int,
    val lastMealTime: Long?,
    val lastComboUpdate: Long?,

    val todayTrainingDone: Boolean,
    val todayNutritionDone: Boolean,
    val todayBonusDone: Boolean,

    val todayMeals: List<LoggedMeal>,
    val achievements: Set<String>
)

data class LoggedMeal(
    val id: Long,
    val text: String,
    val kcal: Int,
    val portion: String?,
    val untracked: Boolean,
    val time: String
)

/** Fresh user — nothing loaded, not onboarded. */
val DEFAULT_USER_STATE = UserState(
    onboarded = false, hero = null, build = null, profile = null,
    nutrition = null, baseline = null, gear = emptySet(),
    programStartEpochMs = null, streak = 0, lastCheckin = null,
    coins = 0, xp = 0, rankPoints = 0, combo = 0,
    lastMealTime = null, lastComboUpdate = null,
    todayTrainingDone = false, todayNutritionDone = false, todayBonusDone = false,
    todayMeals = emptyList(), achievements = emptySet()
)
