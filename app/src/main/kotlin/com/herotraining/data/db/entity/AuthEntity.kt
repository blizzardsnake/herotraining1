package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row table holding the signed-in Google account info (if any). */
@Entity(tableName = "auth")
data class AuthEntity(
    @PrimaryKey val id: Int = 0,
    val uid: String? = null,            // Firebase UID when available, else email hash
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val signedInAt: Long? = null
)
