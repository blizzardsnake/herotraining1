package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_log")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,       // yyyy-MM-dd (one per day; last write wins)
    val weightKg: Double,
    val recordedAt: Long    // epoch-ms
)
