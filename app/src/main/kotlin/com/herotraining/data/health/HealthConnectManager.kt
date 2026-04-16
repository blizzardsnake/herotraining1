package com.herotraining.data.health

import android.content.Context
import android.content.Intent
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class HealthAvailability { INSTALLED, NEEDS_INSTALL, NOT_SUPPORTED }

class HealthConnectManager(private val context: Context) {

    fun availability(): HealthAvailability = when (HealthConnectClient.getSdkStatus(context)) {
        HealthConnectClient.SDK_AVAILABLE -> HealthAvailability.INSTALLED
        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthAvailability.NEEDS_INSTALL
        else -> HealthAvailability.NOT_SUPPORTED
    }

    fun client(): HealthConnectClient? = when (availability()) {
        HealthAvailability.INSTALLED -> HealthConnectClient.getOrCreate(context)
        else -> null
    }

    /** Permissions required by this app. */
    val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        val c = client() ?: return false
        val granted = c.permissionController.getGrantedPermissions()
        return granted.containsAll(requiredPermissions)
    }

    /** Opens Play Store for Health Connect install when missing. */
    fun installIntent(): Intent = Intent(Intent.ACTION_VIEW).apply {
        setPackage("com.android.vending")
        data = android.net.Uri.parse(
            "market://details?id=com.google.android.apps.healthdata"
        )
    }

    suspend fun todaySteps(): Long {
        val c = client() ?: return 0
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = Instant.now()
        return runCatching {
            c.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            ).records.sumOf { it.count }
        }.getOrDefault(0)
    }

    suspend fun todayExerciseSessionsMinutes(): Long {
        val c = client() ?: return 0
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = Instant.now()
        return runCatching {
            c.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            ).records.sumOf { java.time.Duration.between(it.startTime, it.endTime).toMinutes() }
        }.getOrDefault(0)
    }
}
