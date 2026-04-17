package com.herotraining.ui.navigation

object Destinations {
    const val BOOT = "boot"
    const val SIGN_IN = "sign_in"
    const val PROFILE_INTAKE = "profile"              // new anketa entry point
    const val HERO_SELECT = "hero/{gender}"
    const val HERO_GEAR_FORM = "gear/{heroId}"
    const val BUILD_SELECT = "build/{heroId}"
    const val NUTRITION_FORM = "nutrition/{heroId}"
    const val BASELINE_TEST = "baseline/{heroId}"
    const val SUMMARY = "summary/{heroId}"
    const val DASHBOARD = "dashboard"
    const val PROFILE_VIEW = "profile_view"

    fun heroSelect(gender: String) = "hero/$gender"
    fun heroGearForm(heroId: String) = "gear/$heroId"
    fun buildSelect(heroId: String) = "build/$heroId"
    fun nutritionForm(heroId: String) = "nutrition/$heroId"
    fun baselineTest(heroId: String) = "baseline/$heroId"
    fun summary(heroId: String) = "summary/$heroId"
}
