package com.herotraining.domain.calc

import com.herotraining.data.model.HeroBuild

data class Microcycle(
    val id: String, val name: String, val weeks: String, val icon: String,
    val description: String, val repRange: String, val restSec: Int, val volumeMod: Double
)

val MICROCYCLES = listOf(
    Microcycle("strength",    "Силовой акцент", "1-2", "🏋️", "Тяжёлые базовые, 4-6",    "4-6",   150, 0.7),
    Microcycle("hypertrophy", "Гипертрофия",    "3-4", "💪", "Средний вес, 8-12",        "8-12",  75,  1.0),
    Microcycle("endurance",   "Выносливость",   "5-6", "🏃", "15-20 + кардио",           "15-20", 45,  1.3),
    Microcycle("explosive",   "Взрывная сила",  "7-8", "⚡", "Плиометрика",              "3-5",   120, 0.6)
)

fun getCurrentMicrocycle(programStartEpochMs: Long?): Microcycle {
    val start = programStartEpochMs ?: return MICROCYCLES[0]
    val days = (System.currentTimeMillis() - start) / 86_400_000L
    val weekIndex = (days / 7).toInt()
    val cycleIndex = ((weekIndex % 8) / 2)
    return MICROCYCLES[cycleIndex]
}

data class TodayWorkout(val name: String, val focus: String, val dayIdx: Int)

private val SPLITS = mapOf(
    3 to (listOf("Full A", "Full B", "Full C") to listOf("Верх+Кор", "Низ+Кардио", "Общая")),
    4 to (listOf("Push", "Pull", "Legs", "Cond") to listOf("Грудь/плечи", "Спина/бицепс", "Ноги", "Кардио")),
    5 to (listOf("Push", "Pull", "Legs", "Upper", "Cond") to listOf("Грудь", "Спина", "Ноги", "Верх", "Кардио")),
    6 to (listOf("Push H", "Pull H", "Legs H", "Push L", "Pull L", "Legs L") to listOf("Грудь сила", "Спина сила", "Ноги сила", "Грудь объём", "Спина объём", "Ноги объём")),
    7 to (listOf("Push", "Pull", "Legs", "Push", "Pull", "Legs", "Cond") to listOf("Грудь", "Спина", "Ноги", "Грудь 2", "Спина 2", "Ноги 2", "Кардио"))
)

fun getTodayWorkout(programStartEpochMs: Long?, build: HeroBuild): TodayWorkout {
    val split = SPLITS[build.frequency] ?: SPLITS[4]!!
    val start = programStartEpochMs ?: System.currentTimeMillis()
    val days = (System.currentTimeMillis() - start) / 86_400_000L
    val idx = (days % split.first.size).toInt()
    return TodayWorkout(split.first[idx], split.second[idx], idx)
}

data class VolumePrescription(val sets: Int, val reps: Int)

fun calcVolume(baselineReps: Int, build: HeroBuild, cycle: Microcycle): VolumePrescription {
    if (baselineReps == 0) return VolumePrescription(3, 8)
    val mult = build.intensityMultiplier * cycle.volumeMod
    return VolumePrescription(3, (baselineReps * mult * 0.65).toInt().coerceAtLeast(1))
}
