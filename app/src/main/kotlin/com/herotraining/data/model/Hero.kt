package com.herotraining.data.model

import androidx.compose.ui.graphics.Color

/** Macronutrient split (ratios sum to 1.0). */
data class MacroRatio(
    val protein: Double,
    val fat: Double,
    val carb: Double
)

data class FoodItem(val name: String, val kcal: Int)

data class FoodLibrary(
    val breakfast: List<FoodItem>,
    val lunch: List<FoodItem>,
    val dinner: List<FoodItem>,
    val snack: List<FoodItem>,
    val treat: FoodItem
)

data class SignaturePerk(
    val name: String,
    val desc: String,
    val icon: String
)

data class RankSystem(
    val name: String,
    val ranks: List<String>,
    val thresholds: List<Int>,
    val labels: List<String>? = null
)

data class BonusQuest(
    val icon: String,   // emoji or Material icon key
    val title: String,
    val desc: String
)

data class HeroBuild(
    val id: String,
    val name: String,
    val difficulty: String,
    val difficultyNum: Int,
    val description: String,
    val training: String,
    val nutrition: String,
    val philosophy: String,
    val frequency: Int,
    val intensityMultiplier: Double,
    val calorieAdjust: Double,
    val perks: List<String>,
    val hiddenFor45: Boolean = false
)

data class Hero(
    val id: String,
    val name: String,
    val tagline: String,
    val color: Color,
    val bgColor: Color,
    val iconKey: String,           // material icon / emoji
    val gender: Gender,
    val personality: String,
    val description: String,
    val vibe: String,
    val comboName: String,
    val comboStages: List<String>,
    val macroRatio: MacroRatio,
    val signaturePerk: SignaturePerk,
    val foodLibrary: FoodLibrary,
    val rankSystem: RankSystem,
    val bonusQuest: BonusQuest,
    val builds: List<HeroBuild>
)

fun Hero.visibleBuilds(age: Int): List<HeroBuild> =
    builds.filter { !it.hiddenFor45 || age >= 45 }
