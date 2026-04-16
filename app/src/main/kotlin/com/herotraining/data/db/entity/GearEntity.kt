package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One active gear id per row. */
@Entity(tableName = "gear")
data class GearEntity(
    @PrimaryKey val gearId: String
)
