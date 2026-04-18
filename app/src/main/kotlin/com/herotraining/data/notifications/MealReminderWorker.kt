package com.herotraining.data.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Ежедневное напоминание "не забыл записать завтрак/обед/ужин?".
 *
 * Триггерится через PeriodicWorkRequest (раз в 24 часа) с изначальным delay до
 * ближайших 09:00 / 13:00 / 19:00. Текст пуша — простой, мягкий, в режиме наблюдения.
 * Позже (в v0.9+) — заменим на Gemini-генерацию в характере героя.
 */
class MealReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val kind = inputData.getString(KEY_KIND) ?: return Result.failure()

        val (title, body, notifId) = when (kind) {
            KIND_BREAKFAST -> Triple(
                "Завтрак",
                "Записал что съел утром? Любая мелочь — в наблюдение идёт.",
                NOTIF_BREAKFAST
            )
            KIND_LUNCH -> Triple(
                "Обед",
                "Как с обедом? Отметь — собираем твой ритм питания.",
                NOTIF_LUNCH
            )
            KIND_DINNER -> Triple(
                "Ужин",
                "Запиши ужин — на какой час твой организм привык к еде.",
                NOTIF_DINNER
            )
            else -> Triple("Приём пищи", "Не забудь записать что съел.", NOTIF_BREAKFAST)
        }

        HeroNotifications.showMentor(applicationContext, notifId, title, body)
        return Result.success()
    }

    companion object {
        const val KEY_KIND = "kind"
        const val KIND_BREAKFAST = "breakfast"
        const val KIND_LUNCH = "lunch"
        const val KIND_DINNER = "dinner"

        const val NOTIF_BREAKFAST = 1101
        const val NOTIF_LUNCH = 1102
        const val NOTIF_DINNER = 1103
    }
}
