package com.herotraining.data.model

/** Hero signature gear/activity unlocked during onboarding. */
data class HeroGear(
    val id: String,
    val label: String,
    val signature: String,
    val desc: String,
    val icon: String,          // emoji
    val featured: Boolean = false
)
