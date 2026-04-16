package com.herotraining.ui.screens.build

import com.herotraining.R

/**
 * Maps (heroId, buildIndex 0..3) -> drawable resource.
 * buildIndex matches the order of Hero.builds list.
 */
object HeroPortraits {
    fun resFor(heroId: String, buildIndex: Int): Int? = when (heroId) {
        "leon" -> listOf(
            R.drawable.leon_1_rookie,
            R.drawable.leon_2_agent,
            R.drawable.leon_3_peak,
            R.drawable.leon_4_requiem
        )
        "dante" -> listOf(
            R.drawable.dante_1_young,
            R.drawable.dante_2_adult,
            R.drawable.dante_3_peak,
            R.drawable.dante_4_veteran
        )
        "kratos" -> listOf(
            R.drawable.kratos_1_warrior,
            R.drawable.kratos_2_ghost,
            R.drawable.kratos_3_war,
            R.drawable.kratos_4_norse
        )
        "sung_jinwoo" -> listOf(
            R.drawable.jinwoo_1_erank,
            R.drawable.jinwoo_2_awakened,
            R.drawable.jinwoo_3_shadow,
            R.drawable.jinwoo_4_monarch
        )
        "ada" -> listOf(
            R.drawable.ada_1_rookie,
            R.drawable.ada_2_classic,
            R.drawable.ada_3_peak,
            R.drawable.ada_4_handler
        )
        "lara_croft" -> listOf(
            R.drawable.lara_1_young,
            R.drawable.lara_2_survivor,
            R.drawable.lara_3_peak,
            R.drawable.lara_4_classic
        )
        "twob" -> listOf(
            R.drawable.twob_1_standard,
            R.drawable.twob_2_combat,
            R.drawable.twob_3_awakened,
            R.drawable.twob_4_a2
        )
        "ciri" -> listOf(
            R.drawable.ciri_1_trainee,
            R.drawable.ciri_2_hunted,
            R.drawable.ciri_3_zireael,
            R.drawable.ciri_4_veteran
        )
        else -> null
    }?.getOrNull(buildIndex)
}
