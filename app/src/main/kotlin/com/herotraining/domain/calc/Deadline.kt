package com.herotraining.domain.calc

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class DeadlineInfo(val hours: Int, val minutes: Int, val beforeQuestStart: Boolean)

fun getTimeUntilDeadline(): DeadlineInfo {
    val now = LocalDateTime.now(ZoneId.systemDefault())
    val beforeStart = now.hour < 10
    val deadline = when {
        beforeStart -> now.withHour(10).withMinute(0).withSecond(0).withNano(0)
        now.hour < 23 -> now.withHour(23).withMinute(0).withSecond(0).withNano(0)
        else -> now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0)
    }
    val seconds = ChronoUnit.SECONDS.between(now, deadline)
    val hours = (seconds / 3600).toInt()
    val minutes = ((seconds % 3600) / 60).toInt()
    return DeadlineInfo(hours, minutes, beforeStart)
}
