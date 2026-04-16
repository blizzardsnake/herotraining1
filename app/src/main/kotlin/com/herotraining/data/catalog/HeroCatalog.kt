package com.herotraining.data.catalog

import com.herotraining.data.model.Gender
import com.herotraining.data.model.Hero

object HeroCatalog {
    val MALE: List<Hero> = MaleHeroes.ALL
    val FEMALE: List<Hero> = FemaleHeroes.ALL
    val ALL: List<Hero> = MALE + FEMALE

    private val byId: Map<String, Hero> = ALL.associateBy { it.id }
    fun byId(id: String): Hero? = byId[id]

    fun forGender(gender: Gender): List<Hero> = when (gender) {
        Gender.MALE -> MALE
        Gender.FEMALE -> FEMALE
    }
}
