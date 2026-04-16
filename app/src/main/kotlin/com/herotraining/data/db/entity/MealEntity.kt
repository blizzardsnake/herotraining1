package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One logged meal for a given calendar day (yyyy-MM-dd). */
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: String,           // yyyy-MM-dd
    val time: String,          // HH:mm
    val text: String,
    val kcal: Int,
    val portion: String? = null,   // S/M/L/XL/NONE
    val untracked: Boolean = false,
    val loggedAt: Long             // epoch-ms
)
