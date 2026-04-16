package com.herotraining.domain.calc

import androidx.compose.ui.graphics.Color

enum class BmiCategory(val label: String, val color: Color) {
    UNDERWEIGHT("Недовес",    Color(0xFF3B82F6)),  // blue
    NORMAL     ("Норма",      Color(0xFF10B981)),  // green
    OVERWEIGHT ("Избыток",    Color(0xFFE6B800)),  // amber
    OBESE_1    ("Ожирение I",  Color(0xFFE6761A)),  // orange
    OBESE_2    ("Ожирение II", Color(0xFFDC2626)),  // red
    OBESE_3    ("Ожирение III",Color(0xFF7F1D1D)); // dark red

    companion object {
        fun classify(bmi: Double): BmiCategory = when {
            bmi < 18.5 -> UNDERWEIGHT
            bmi < 25.0 -> NORMAL
            bmi < 30.0 -> OVERWEIGHT
            bmi < 35.0 -> OBESE_1
            bmi < 40.0 -> OBESE_2
            else -> OBESE_3
        }
    }
}

data class BmiResult(
    val value: Double,     // BMI index
    val category: BmiCategory,
    val idealMin: Int,     // kg — lower bound of normal for given height
    val idealMax: Int      // kg — upper bound of normal
)

/** Returns null if input insufficient. */
fun calcBmi(weightKg: Int?, heightCm: Int?): BmiResult? {
    if (weightKg == null || heightCm == null || heightCm <= 0) return null
    val heightM = heightCm / 100.0
    val bmi = weightKg / (heightM * heightM)
    val idealMin = (18.5 * heightM * heightM).toInt()
    val idealMax = (24.9 * heightM * heightM).toInt()
    return BmiResult(
        value = (bmi * 10).toInt() / 10.0,  // one decimal
        category = BmiCategory.classify(bmi),
        idealMin = idealMin,
        idealMax = idealMax
    )
}
