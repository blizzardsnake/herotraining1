package com.herotraining.data.model

/** Mirrors the prototype's `profile` state shape. */
data class Profile(
    val age: Int,
    val weight: Int,    // kg
    val height: Int,    // cm
    val sex: Gender,
    val experience: Experience,
    val equipment: EquipmentKind,
    val timePerSessionMinutes: Int, // 30/45/60/90
    val injuries: Set<Injury>
)

enum class Experience(val key: String, val label: String) {
    NONE("none", "Никогда"),
    BEGINNER("beginner", "< полугода"),
    INTERMEDIATE("intermediate", "6 мес - 2 года"),
    ADVANCED("advanced", "2+ года"),
    RETURNING("returning", "Был опыт, не в форме");

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}

enum class EquipmentKind(val key: String, val label: String) {
    GYM("gym", "Фитнес-клуб"),
    HOME_FULL("home_full", "Дом. зал"),
    HOME_LIGHT("home_light", "Дом лайт"),
    BODYWEIGHT("bodyweight", "Только тело");

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}

enum class Injury(val key: String, val label: String) {
    NONE("none", "Здоров"),
    KNEES("knees", "Колени"),
    BACK("back", "Спина"),
    SHOULDERS("shoulders", "Плечи"),
    CARDIO("cardio", "Сердце"),
    OTHER("other", "Другое");

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}
