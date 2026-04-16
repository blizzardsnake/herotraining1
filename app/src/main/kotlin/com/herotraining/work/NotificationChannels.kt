package com.herotraining.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val QUESTS = "hero_quests"

    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        if (nm.getNotificationChannel(QUESTS) != null) return
        val channel = NotificationChannel(
            QUESTS, "Квесты", NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Старт квеста в 10:00 и напоминание о дедлайне в 20:00"
        }
        nm.createNotificationChannel(channel)
    }
}
