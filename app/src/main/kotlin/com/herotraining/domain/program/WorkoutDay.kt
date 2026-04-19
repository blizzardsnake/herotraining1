package com.herotraining.domain.program

import com.herotraining.data.catalog.exercises.Exercise

/** Один элемент сгенерированной тренировки. */
data class ExerciseBlock(
    val exercise: Exercise,
    val sets: Int,
    /** "8-12" для reps, "45" для time_sec, "10" для time_min — смотри exercise.unit */
    val prescription: String,
    val restSec: Int,
    /** Короткий hero-flavored совет: "Как Кратос, жёстко", "В темпе Данте" */
    val flavorNote: String? = null
)

/** Полная тренировка дня. */
data class WorkoutDay(
    /** "ПУШ · ГРУДЬ & ПЛЕЧИ" */
    val title: String,
    /** "HEAVY PUSH PROTOCOL" — English подзаголовок в стиле приложения */
    val subtitle: String,
    /** ~35 минут — оценка чтобы юзер знал сколько займёт */
    val estimatedMinutes: Int,
    /** Упражнения по порядку — на какой сегодня фокус */
    val blocks: List<ExerciseBlock>,
    /** Короткая цитата или совет дня (философия билда, тон героя) */
    val mantra: String? = null
)
