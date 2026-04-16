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
import com.herotraining.MainActivity
import com.herotraining.R

/**
 * Runs once per day at 10:00 local time (scheduled via WorkScheduler).
 * Fires "Quest started" notification, then self-reschedules for next 10:00.
 */
class QuestStartWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        postNotification()
        // Reschedule next-day 10:00
        WorkScheduler.scheduleDailyQuestStart(applicationContext)
        return Result.success()
    }

    private fun postNotification() {
        NotificationChannels.ensureCreated(applicationContext)
        val pending = PendingIntent.getActivity(
            applicationContext, 0,
            Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.QUESTS)
            .setContentTitle("[ КВЕСТ ] Протокол активирован")
            .setContentText("Тренировка · Питание · Бонус. Дедлайн 23:00.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) return
        NotificationManagerCompat.from(applicationContext).notify(1001, notification)
    }
}
