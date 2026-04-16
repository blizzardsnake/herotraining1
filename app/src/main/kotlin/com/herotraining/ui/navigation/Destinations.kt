package com.herotraining.ui.navigation

/** All onboarding + main screens. */
object Destinations {
    const val GENDER_SELECT = "gender"
    const val HERO_SELECT = "hero/{gender}"
    const val PROFILE_FORM = "profile/{gender}/{heroId}"
    const val HERO_GEAR_FORM = "gear/{heroId}"
    const val BUILD_SELECT = "build/{heroId}"
    const val NUTRITION_FORM = "nutrition/{heroId}"
    const val BASELINE_TEST = "baseline/{heroId}"
    const val SUMMARY = "summary/{heroId}/{buildId}"
    const val DASHBOARD = "dashboard"

    fun heroSelect(gender: String) = "hero/$gender"
    fun profileForm(gender: String, heroId: String) = "profile/$gender/$heroId"
    fun heroGearForm(heroId: String) = "gear/$heroId"
    fun buildSelect(heroId: String) = "build/$heroId"
    fun nutritionForm(heroId: String) = "nutrition/$heroId"
    fun baselineTest(heroId: String) = "baseline/$heroId"
    fun summary(heroId: String, buildId: String) = "summary/$heroId/$buildId"
}
