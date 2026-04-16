package com.herotraining.data.model

data class NutritionProfile(
    val style: FoodStyle,
    val exclusions: Set<Exclusion>,
    val goal: NutritionGoal,
    val mealsPerDay: Int,
    val keepTreats: Set<TreatKind>
)

enum class FoodStyle(val key: String, val label: String) {
    OMNIVORE("omnivore", "Всеядный"),
    PESCATARIAN("pescatarian", "Пескетарианец"),
    VEGETARIAN("vegetarian", "Вегетарианец"),
    VEGAN("vegan", "Веган"),
    KETO("keto", "Кето-лайт");

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}

enum class Exclusion(val key: String, val label: String) {
    NONE("none", "Ничего"),
    DAIRY("dairy", "Молочка"),
    GLUTEN("gluten", "Глютен"),
    NUTS("nuts", "Орехи"),
    SEAFOOD("seafood", "Морепродукты"),
    EGGS("eggs", "Яйца");

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}

enum class NutritionGoal(val key: String, val label: String) {
    LOSE("lose", "Сбросить"),
    MAINTAIN("maintain", "Держать"),
    GAIN("gain", "Набрать");

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}

enum class TreatKind(val key: String, val label: String) {
    SWEETS("sweets", "Сладкое"),
    PIZZA("pizza", "Пицца"),
    BURGERS("burgers", "Бургеры"),
    BREAD("bread", "Хлеб"),
    COFFEE("coffee", "Кофе с сахаром"),
    NONE("none", "Аскеза");

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}
