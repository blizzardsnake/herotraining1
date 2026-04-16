package com.herotraining.data.model

/** Results of the 7-exercise baseline fitness test. Values are raw (reps or seconds). */
data class Baseline(
    val pushups: Int = 0,
    val squats: Int = 0,
    val plankSec: Int = 0,
    val pullups: Int = 0,
    val burpees: Int = 0,
    val cardioMinutes: Int = 0,
    val flexibilityScale: Int = 0   // 1..5
)
