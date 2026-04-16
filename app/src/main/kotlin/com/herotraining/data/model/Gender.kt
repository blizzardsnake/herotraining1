package com.herotraining.data.model

enum class Gender(val key: String) {
    MALE("male"),
    FEMALE("female");

    companion object {
        fun fromKey(key: String?): Gender? = entries.firstOrNull { it.key == key }
    }
}
