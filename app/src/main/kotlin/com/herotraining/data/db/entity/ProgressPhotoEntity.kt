package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_photos")
data class ProgressPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                // yyyy-MM-dd
    val localUri: String,            // content:// or file:// to the stored image
    val pose: String? = null,        // front / side / back
    val note: String? = null,
    val recordedAt: Long
)
