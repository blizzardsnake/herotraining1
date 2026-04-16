package com.herotraining.data.catalog

import com.herotraining.data.model.Injury

enum class TestKind { REPS, TIME_SEC, TIME_MIN, SCALE }

data class TestExercise(
    val id: String,
    val name: String,
    val description: String,
    val instruction: String,
    val unit: String,
    val kind: TestKind,
    val iconKey: String,
    val excludeIf: Set<Injury> = emptySet(),
    val scaleOptions: List<Pair<Int, String>> = emptyList()
)

object TestExerciseCatalog {
    val ALL = listOf(
        TestExercise("pushups",     "Отжимания",        "Макс за подход", "С пола.",      "раз", TestKind.REPS, "activity"),
        TestExercise("squats",      "Приседания",       "Макс за подход", "Без веса.",    "раз", TestKind.REPS, "activity"),
        TestExercise("plank",       "Планка",           "Макс время",     "На локтях.",   "сек", TestKind.TIME_SEC, "timer"),
        TestExercise("pullups",     "Подтягивания",     "Макс",           "Прямой хват.", "раз", TestKind.REPS, "activity",
            excludeIf = setOf(Injury.SHOULDERS)),
        TestExercise("burpees",     "Бёрпи 1 мин",      "За 60 сек",      "Полный цикл.", "раз", TestKind.REPS, "activity",
            excludeIf = setOf(Injury.KNEES, Injury.CARDIO)),
        TestExercise("cardio",      "Бег 1 км",         "Время",          "Бег/ходьба.",  "мин", TestKind.TIME_MIN, "heart"),
        TestExercise("flexibility", "Наклон",           "Шкала",          "Стоя.",        "балл", TestKind.SCALE, "activity",
            scaleOptions = listOf(
                1 to "Не до колен",
                2 to "До голени",
                3 to "До щиколоток",
                4 to "До пальцев",
                5 to "Ладонями"
            ))
    )

    fun forInjuries(injuries: Set<Injury>): List<TestExercise> =
        ALL.filter { ex -> ex.excludeIf.none { it in injuries } }
}
