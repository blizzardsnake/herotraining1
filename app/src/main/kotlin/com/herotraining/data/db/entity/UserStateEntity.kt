package com.herotraining.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.herotraining.data.model.Baseline
import com.herotraining.data.model.Gender
import com.herotraining.data.model.NutritionGoal
import com.herotraining.data.model.NutritionProfile

/**
 * Single-row table holding global app state. Matches the prototype's `defaultState` shape.
 * `id` is always 0 (enforced at app layer); Flow<UserStateEntity?> is how we observe changes.
 */
@Entity(tableName = "user_state")
data class UserStateEntity(
    @PrimaryKey val id: Int = 0,

    // Onboarding
    val onboarded: Boolean = false,
    val heroId: String? = null,
    val buildId: String? = null,

    // Profile
    val age: Int? = null,
    val weight: Int? = null,
    val height: Int? = null,
    val sex: String? = null,                 // Gender.key
    val experience: String? = null,          // Experience.key
    val equipment: String? = null,           // EquipmentKind.key
    val timePerSession: Int? = null,
    val injuries: String = "",               // comma-separated Injury.key

    // Nutrition profile
    val foodStyle: String? = null,
    val exclusions: String = "",
    val goal: String? = null,
    val mealsPerDay: Int? = null,
    val keepTreats: String = "",

    // Baseline
    val basePushups: Int = 0,
    val baseSquats: Int = 0,
    val basePlankSec: Int = 0,
    val basePullups: Int = 0,
    val baseBurpees: Int = 0,
    val baseCardioMinutes: Int = 0,
    val baseFlexibility: Int = 0,

    // Program timeline
    val programStartDate: Long? = null,      // epoch-ms
    val streak: Int = 0,
    val lastCheckin: String? = null,         // yyyy-MM-dd (LocalDate.toString())

    // Economy
    val coins: Int = 0,
    val xp: Int = 0,
    val rankPoints: Int = 0,

    // Combo
    val combo: Int = 0,
    val lastMealTime: Long? = null,
    val lastComboUpdate: Long? = null,

    // Today’s quest status
    val todayTrainingDone: Boolean = false,
    val todayNutritionDone: Boolean = false,
    val todayBonusDone: Boolean = false
)
