package com.herotraining.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Schedules the daily 10:00 quest-start and 20:00 deadline-reminder notifications.
 * Each worker self-reschedules after running — so we just call these once on app start.
 */
object WorkScheduler {

    private const val QUEST_START = "quest_start_daily"
    private const val DEADLINE = "deadline_reminder_daily"

    fun scheduleAll(context: Context) {
        scheduleDailyQuestStart(context)
        scheduleDailyDeadlineReminder(context)
    }

    fun scheduleDailyQuestStart(context: Context) {
        val delay = delayUntilHour(10)
        val work = OneTimeWorkRequestBuilder<QuestStartWorker>()
            .setInitialDelay(delay.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(QUEST_START, ExistingWorkPolicy.REPLACE, work)
    }

    fun scheduleDailyDeadlineReminder(context: Context) {
        val delay = delayUntilHour(20)
        val work = OneTimeWorkRequestBuilder<DeadlineReminderWorker>()
            .setInitialDelay(delay.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(DEADLINE, ExistingWorkPolicy.REPLACE, work)
    }

    private fun delayUntilHour(targetHour: Int): Duration {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        var target = now.withHour(targetHour).withMinute(0).withSecond(0).withNano(0)
        if (!target.isAfter(now)) target = target.plusDays(1)
        return Duration.between(now, target)
    }
}
