package com.herotraining.domain.schedule

import java.util.Calendar

/**
 * Onboarding-to-Day-1 unlock math.
 *
 * Per product vision: после завершения онбординга первая тренировка "всегда завтра".
 * Даже если юзер закончил в 07:00 утра — тренировка только **в 10:00 ЗАВТРА**, не сегодня.
 * Это намеренный якорь "начну с понедельника", обёрнутый в игровую механику.
 *
 * Утром (в 06:00 следующего дня) разблокируется план питания.
 * В 10:00 следующего дня — тренировка.
 */
object Unlocks {

    const val NUTRITION_UNLOCK_HOUR = 6
    const val TRAINING_UNLOCK_HOUR = 10

    /** Unix-ms момент когда станет доступен план питания на Day 1. */
    fun nutritionUnlockAt(programStartEpochMs: Long): Long =
        dayAfterAt(programStartEpochMs, NUTRITION_UNLOCK_HOUR)

    /** Unix-ms момент когда разблокируется тренировка Day 1. */
    fun trainingUnlockAt(programStartEpochMs: Long): Long =
        dayAfterAt(programStartEpochMs, TRAINING_UNLOCK_HOUR)

    /** true пока не наступил первый тренировочный анлок. */
    fun isBeforeFirstTraining(programStartEpochMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean =
        nowMs < trainingUnlockAt(programStartEpochMs)

    /** true пока не наступил первый нутришн-анлок. */
    fun isBeforeFirstNutrition(programStartEpochMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean =
        nowMs < nutritionUnlockAt(programStartEpochMs)

    /**
     * "Следующий день, в указанный час по локальному времени".
     *   programStart 23:50 Mon -> Tue 10:00 (or 06:00)
     *   programStart 09:00 Mon -> Tue 10:00 (or 06:00) — НЕ сегодня
     */
    private fun dayAfterAt(programStartEpochMs: Long, hour: Int): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = programStartEpochMs
            add(Calendar.DAY_OF_MONTH, 1)       // always next calendar day
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
