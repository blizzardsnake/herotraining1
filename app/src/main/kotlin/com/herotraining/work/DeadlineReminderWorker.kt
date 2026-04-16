package com.herotraining.work

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.herotraining.HeroApp
import com.herotraining.MainActivity
import com.herotraining.R

/**
 * Runs once per day at 20:00. Checks unfinished quests; if something is pending -> warns user.
 */
class DeadlineReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as HeroApp
        val s = app.stateRepository.snapshot()
        val pending = buildList {
            if (!s.todayTrainingDone) add("Тренировка")
            if (!s.todayNutritionDone) add("Питание")
            if (!s.todayBonusDone) add("Бонус")
        }
        if (pending.isNotEmpty()) {
            postNotification(pending)
        }
        WorkScheduler.scheduleDailyDeadlineReminder(applicationContext)
        return Result.success()
    }

    private fun postNotification(pending: List<String>) {
        NotificationChannels.ensureCreated(applicationContext)
        val pi = PendingIntent.getActivity(
            applicationContext, 1,
            Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notif = NotificationCompat.Builder(applicationContext, NotificationChannels.QUESTS)
            .setContentTitle("[ ДЕДЛАЙН ] 3 часа до закрытия")
            .setContentText("Осталось: " + pending.joinToString(" · "))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) return
        NotificationManagerCompat.from(applicationContext).notify(1002, notif)
    }
}
