package com.herotraining.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.herotraining.MainActivity
import com.herotraining.R

/**
 * Единственная точка для показа локальных уведомлений от ментора.
 *
 * Каналы создаются лениво при первом показе (нет смысла блокировать старт app).
 * Если пермиссия POST_NOTIFICATIONS не выдана (Android 13+) — .notify() молча
 * игнорится, без исключений.
 */
object HeroNotifications {

    const val CHANNEL_MENTOR = "mentor"
    const val CHANNEL_MENTOR_NAME = "Наставник"
    const val CHANNEL_MENTOR_DESC = "Утренний план питания, разблок тренировки, шейминг за пропуски"

    const val NOTIF_NUTRITION_MORNING = 1001
    const val NOTIF_TRAINING_UNLOCK = 1002
    const val NOTIF_TEST = 1099

    fun ensureChannels(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = ctx.getSystemService(NotificationManager::class.java) ?: return
            if (mgr.getNotificationChannel(CHANNEL_MENTOR) == null) {
                val channel = NotificationChannel(
                    CHANNEL_MENTOR,
                    CHANNEL_MENTOR_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_MENTOR_DESC
                    enableLights(true)
                    enableVibration(true)
                }
                mgr.createNotificationChannel(channel)
            }
        }
    }

    fun showMentor(
        ctx: Context,
        id: Int,
        title: String,
        body: String
    ) {
        ensureChannels(ctx)

        val openAppIntent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            ctx, id, openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notif = NotificationCompat.Builder(ctx, CHANNEL_MENTOR)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        runCatching {
            NotificationManagerCompat.from(ctx).notify(id, notif)
        }
    }
}
