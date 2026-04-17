package com.herotraining.ui.navigation

object Destinations {
    const val BOOT = "boot"
    const val SIGN_IN = "sign_in"

    // User-visible ritual gate — must accept before any personal data is asked.
    const val DISCLAIMER = "disclaimer"

    // Anketa (profile) — age/height/weight/sex/experience/equipment/injuries
    const val PROFILE_INTAKE = "profile"

    // Nutrition + baseline now happen BEFORE hero select (they're hero-agnostic data).
    // These routes carry no heroId so they can be reached from onboarding or edit modes.
    const val NUTRITION_INTAKE = "nutrition_intake"
    const val BASELINE_INTAKE = "baseline_intake"

    // Hero + gear + build choice — happens AFTER the data above is collected.
    const val HERO_SELECT = "hero/{gender}"
    const val HERO_GEAR_FORM = "gear/{heroId}"
    const val BUILD_SELECT = "build/{heroId}"

    // "Тест зачтён — первая миссия завтра в 10:00" — final onboarding checkpoint.
    const val TOMORROW = "tomorrow/{heroId}"

    const val DASHBOARD = "dashboard"
    const val PROFILE_VIEW = "profile_view"

    fun heroSelect(gender: String) = "hero/$gender"
    fun heroGearForm(heroId: String) = "gear/$heroId"
    fun buildSelect(heroId: String) = "build/$heroId"
    fun tomorrow(heroId: String) = "tomorrow/$heroId"
}
