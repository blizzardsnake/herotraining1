package com.herotraining.domain.calc

import com.herotraining.data.model.Hero
import com.herotraining.data.model.UserState
import java.time.LocalDateTime
import java.time.ZoneId

data class ComboStage(val name: String, val index: Int)
data class ComboBonus(val xpMult: Double, val label: String, val desc: String)

fun getComboStage(combo: Int, hero: Hero): ComboStage {
    val stages = hero.comboStages
    val stageSize = 100.0 / stages.size
    val idx = (combo / stageSize).toInt().coerceAtMost(stages.size - 1)
    return ComboStage(stages[idx], idx)
}

fun getComboBonus(combo: Int): ComboBonus = when {
    combo >= 100 -> ComboBonus(2.0, "HERO MODE", "x2 XP · treat без штрафа")
    combo >= 75 -> ComboBonus(1.5, "ON FIRE", "+50% XP · treat без штрафа")
    combo >= 50 -> ComboBonus(1.25, "HEATED", "+25% XP")
    combo >= 25 -> ComboBonus(1.1, "WARMING UP", "+10% XP")
    else -> ComboBonus(1.0, "COLD", "Нет бонусов")
}

/** Applies time-based combo decay (same rules as prototype). */
fun getDecayedCombo(state: UserState): Int {
    val lastUpdate = state.lastComboUpdate ?: return state.combo
    val now = LocalDateTime.now(ZoneId.systemDefault())
    val hour = now.hour
    // No decay during 22:00..08:00
    if (hour >= 22 || hour < 8) return state.combo
    val lastMeal = state.lastMealTime ?: return state.combo
    val hoursSinceMeal = (System.currentTimeMillis() - lastMeal) / 3_600_000.0
    return if (hoursSinceMeal > 4) {
        (state.combo - ((hoursSinceMeal - 4) * 3).toInt()).coerceAtLeast(0)
    } else state.combo
}
