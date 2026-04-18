package com.herotraining.data.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.herotraining.domain.schedule.Unlocks
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Планирует разовые WorkManager-задачи которые в нужный момент покажут пуш.
 *
 * Используем WorkManager, а не AlarmManager, по двум причинам:
 *   - setExactAndAllowWhileIdle требует доп. разрешения на Android 12+ которое юзер
 *     должен разрешить вручную в настройках. Не хотим блокировать UX на этом.
 *   - Наша точность ±15 мин приемлема — "герой не ждёт но тренировка ждёт тебя
 *     где-то в районе 10 утра" это нормальный UX.
 */
object MentorScheduler {

    private const val WORK_NUTRITION_DAY1 = "mentor.nutrition.day1"
    private const val WORK_TRAINING_DAY1 = "mentor.training.day1"
    private const val WORK_TEST = "mentor.test"
    private const val WORK_MEAL_BREAKFAST = "meal.breakfast"
    private const val WORK_MEAL_LUNCH = "meal.lunch"
    private const val WORK_MEAL_DINNER = "meal.dinner"

    /**
     * Планирует два пуша для Day 1:
     *   - 06:00 — план питания разблокирован
     *   - 10:00 — тренировка разблокирована
     *
     * Если момент уже прошёл (юзер закончил онбординг после 10:00 — теоретически
     * невозможно из-за флоу, но страхуемся) — пропускаем этот пуш.
     */
    fun scheduleFirstDay(ctx: Context, programStartEpochMs: Long) {
        val now = System.currentTimeMillis()
        val nutritionAt = Unlocks.nutritionUnlockAt(programStartEpochMs)
        val trainingAt = Unlocks.trainingUnlockAt(programStartEpochMs)

        if (nutritionAt > now) {
            schedule(ctx, nutritionAt - now, MentorPushWorker.KIND_NUTRITION, WORK_NUTRITION_DAY1)
        }
        if (trainingAt > now) {
            schedule(ctx, trainingAt - now, MentorPushWorker.KIND_TRAINING, WORK_TRAINING_DAY1)
        }
    }

    /** Отменяет всё запланированное (вызывается при hardReset / смене героя). */
    fun cancelAll(ctx: Context) {
        val wm = WorkManager.getInstance(ctx)
        wm.cancelUniqueWork(WORK_NUTRITION_DAY1)
        wm.cancelUniqueWork(WORK_TRAINING_DAY1)
        wm.cancelUniqueWork(WORK_MEAL_BREAKFAST)
        wm.cancelUniqueWork(WORK_MEAL_LUNCH)
        wm.cancelUniqueWork(WORK_MEAL_DINNER)
    }

    /**
     * Планирует ежедневные напоминания "запиши приём пищи" на 09:00/13:00/19:00.
     * Каждое — PeriodicWorkRequest с интервалом 24ч.
     *
     * Вызывается из completeOnboarding сразу после scheduleFirstDay().
     * Если вызывается повторно (KEEP policy) — существующий цикл не перебивается.
     */
    fun scheduleDailyMealReminders(ctx: Context) {
        scheduleDailyAt(ctx, 9, MealReminderWorker.KIND_BREAKFAST, WORK_MEAL_BREAKFAST)
        scheduleDailyAt(ctx, 13, MealReminderWorker.KIND_LUNCH, WORK_MEAL_LUNCH)
        scheduleDailyAt(ctx, 19, MealReminderWorker.KIND_DINNER, WORK_MEAL_DINNER)
    }

    private fun scheduleDailyAt(ctx: Context, hourOfDay: Int, kind: String, uniqueName: String) {
        val delay = millisUntilNext(hourOfDay)
        val req = PeriodicWorkRequestBuilder<MealReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(MealReminderWorker.KEY_KIND, kind).build())
            .build()
        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
            uniqueName, ExistingPeriodicWorkPolicy.KEEP, req
        )
    }

    /** Возвращает сколько миллисекунд до ближайшего [hourOfDay] по локальному времени. */
    private fun millisUntilNext(hourOfDay: Int): Long {
        val now = Calendar.getInstance()
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) target.add(Calendar.DAY_OF_MONTH, 1)
        return target.timeInMillis - now.timeInMillis
    }

    /**
     * Тест-триггер — запускает worker "прямо сейчас" чтобы юзер мог убедиться
     * что пуши работают, не дожидаясь 06:00 следующего дня. Виден из Profile screen.
     */
    fun triggerTestPush(ctx: Context, kind: String = MentorPushWorker.KIND_TRAINING) {
        val req = OneTimeWorkRequestBuilder<MentorPushWorker>()
            .setInputData(Data.Builder().putString(MentorPushWorker.KEY_KIND, kind).build())
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            WORK_TEST, ExistingWorkPolicy.REPLACE, req
        )
    }

    private fun schedule(ctx: Context, delayMs: Long, kind: String, uniqueName: String) {
        val req = OneTimeWorkRequestBuilder<MentorPushWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(MentorPushWorker.KEY_KIND, kind).build())
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            uniqueName, ExistingWorkPolicy.REPLACE, req
        )
    }
}
