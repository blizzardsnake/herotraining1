package com.herotraining.domain.calc

import com.herotraining.data.model.Gender
import com.herotraining.data.model.Hero
import com.herotraining.data.model.HeroBuild
import com.herotraining.data.model.Profile
import kotlin.math.roundToInt

data class Macros(
    val calories: Int,
    val tdee: Int,
    val protein: Int,
    val fat: Int,
    val carb: Int
)

/** Mifflin–St Jeor equation. Returns basal metabolic rate. */
fun calcBMR(profile: Profile): Double {
    val w = profile.weight.toDouble()
    val h = profile.height.toDouble()
    val a = profile.age.toDouble()
    val base = 10 * w + 6.25 * h - 5 * a
    return if (profile.sex == Gender.MALE) base + 5 else base - 161
}

/** Applies training-frequency activity factor (same mapping as prototype). */
fun calcTDEE(profile: Profile, frequency: Int): Int {
    val factor = when {
        frequency >= 6 -> 1.725
        frequency >= 4 -> 1.55
        frequency >= 3 -> 1.375
        else -> 1.2
    }
    return (calcBMR(profile) * factor).roundToInt()
}

fun calcMacros(profile: Profile, build: HeroBuild, hero: Hero): Macros {
    val tdee = calcTDEE(profile, build.frequency)
    val cal = (tdee * (1 + build.calorieAdjust)).roundToInt()
    val r = hero.macroRatio
    return Macros(
        calories = cal,
        tdee = tdee,
        protein = (cal * r.protein / 4).roundToInt(),
        fat = (cal * r.fat / 9).roundToInt(),
        carb = (cal * r.carb / 4).roundToInt()
    )
}
