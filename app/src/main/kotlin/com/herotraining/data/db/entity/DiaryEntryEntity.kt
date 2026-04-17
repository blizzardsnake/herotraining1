package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                // yyyy-MM-dd
    val mood: Int? = null,           // 1..5 scale (optional)
    val energy: Int? = null,         // 1..5
    val text: String,                // free-form note
    val recordedAt: Long
)
