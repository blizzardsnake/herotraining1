package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One snapshot of body measurements taken on a given day. */
@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                // yyyy-MM-dd
    val chestCm: Double? = null,
    val waistCm: Double? = null,
    val hipsCm: Double? = null,
    val bicepCm: Double? = null,
    val thighCm: Double? = null,
    val neckCm: Double? = null,
    val recordedAt: Long             // epoch-ms
)
