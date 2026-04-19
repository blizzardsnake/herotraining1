package com.herotraining.data.catalog.exercises

import com.herotraining.data.model.EquipmentKind
import com.herotraining.data.model.Injury

/**
 * Группа мышц упражнения. Используется генератором чтобы подобрать набор под день
 * (Push → {PUSH_CHEST, PUSH_SHOULDERS, PUSH_TRICEPS}, Pull → {PULL_BACK, PULL_BICEPS}, ...).
 */
enum class BodyPart(val label: String) {
    PUSH_CHEST("Грудь"),
    PUSH_SHOULDERS("Плечи"),
    PUSH_TRICEPS("Трицепс"),
    PULL_BACK("Спина"),
    PULL_BICEPS("Бицепс"),
    LEGS_QUADS("Квадрицепс"),
    LEGS_HAMSTRINGS("Задняя бедра"),
    LEGS_GLUTES("Ягодицы"),
    LEGS_CALVES("Икры"),
    CORE_ABS("Пресс"),
    CORE_OBLIQUES("Косые"),
    CARDIO("Кардио"),
    MOBILITY("Мобильность")
}

/** Требования инвентаря. Упражнению нужен ЛЮБОЙ элемент из `gearAny`. */
enum class GearKind(val label: String) {
    BODYWEIGHT("Собственный вес"),
    DUMBBELLS("Гантели"),
    BARBELL("Штанга"),
    PULL_BAR("Турник"),
    BENCH("Скамья"),
    BAND("Резинка"),
    ROPE("Скакалка"),
    OUTDOOR("Открытая местность")
}

enum class ExerciseDifficulty(val label: String) {
    BEGINNER("Новичок"),
    INTERMEDIATE("Средний"),
    ADVANCED("Продвинутый")
}

enum class ExerciseUnit(val label: String, val short: String) {
    REPS("повторы", "×"),       // "3×12"
    TIME_SEC("секунды", "с"),   // "3×45 с"
    DISTANCE_M("метры", "м"),   // "200 м"
    TIME_MIN("минуты", "мин")   // "10 мин"
}

/**
 * Одно упражнение из каталога.
 *
 * @param baselineId — ключ того же имени что в [TestExercises] если есть. Если тест
 *   вернул значение для этого упражнения — генератор скейлит объём от baseline'а.
 *   Иначе — использует дефолт (reps per microcycle).
 * @param excludeIf — не показывать упражнение если у юзера хоть одна из этих травм.
 */
data class Exercise(
    val id: String,
    val nameRu: String,
    val nameEn: String,
    val primary: BodyPart,
    val secondary: List<BodyPart> = emptyList(),
    val gearAny: Set<GearKind>,        // хотя бы один из этих — и упражнение доступно
    val difficulty: ExerciseDifficulty = ExerciseDifficulty.INTERMEDIATE,
    val unit: ExerciseUnit = ExerciseUnit.REPS,
    val instructionRu: String,
    val baselineId: String? = null,     // "pushups", "squats", "pullups", ...
    val excludeIf: Set<Injury> = emptySet()
) {
    /** Упражнение доступно для юзера на данном снаряжении. */
    fun fitsEquipment(equipment: EquipmentKind): Boolean {
        val owned = gearKindsFor(equipment)
        return gearAny.any { it in owned }
    }

    /** true если упражнение пропускается из-за травм. */
    fun blockedByInjuries(injuries: Set<Injury>): Boolean =
        excludeIf.any { it in injuries }
}

/**
 * Мэппинг из EquipmentKind юзера (ответ в анкете) в реальный список инвентаря.
 * BODYWEIGHT  → ничего кроме тела
 * HOME_LIGHT  → тело + гантели + резинка + скакалка + стул/скамья
 * HOME_FULL   → + штанга + турник
 * GYM         → всё
 */
fun gearKindsFor(equipment: EquipmentKind): Set<GearKind> = when (equipment) {
    EquipmentKind.BODYWEIGHT -> setOf(GearKind.BODYWEIGHT)
    EquipmentKind.HOME_LIGHT -> setOf(
        GearKind.BODYWEIGHT, GearKind.DUMBBELLS, GearKind.BAND,
        GearKind.ROPE, GearKind.BENCH
    )
    EquipmentKind.HOME_FULL -> setOf(
        GearKind.BODYWEIGHT, GearKind.DUMBBELLS, GearKind.BARBELL, GearKind.BENCH,
        GearKind.PULL_BAR, GearKind.BAND, GearKind.ROPE
    )
    EquipmentKind.GYM -> GearKind.entries.toSet()
}
