package com.herotraining.domain.program

import com.herotraining.data.catalog.exercises.BodyPart
import com.herotraining.data.catalog.exercises.Exercise
import com.herotraining.data.catalog.exercises.ExerciseLibrary
import com.herotraining.data.catalog.exercises.ExerciseUnit
import com.herotraining.data.model.Baseline
import com.herotraining.data.model.HeroBuild
import com.herotraining.data.model.Injury
import com.herotraining.data.model.UserState
import com.herotraining.domain.calc.Microcycle
import com.herotraining.domain.calc.getCurrentMicrocycle
import com.herotraining.domain.calc.getTodayWorkout

/**
 * "Какие группы мышц сегодня" — определяется по split-дню из существующих SPLITS.
 * Маппинг строк split'а в набор [BodyPart].
 */
private enum class DayType(val parts: Set<BodyPart>, val title: String, val subtitle: String) {
    PUSH(
        setOf(BodyPart.PUSH_CHEST, BodyPart.PUSH_SHOULDERS, BodyPart.PUSH_TRICEPS),
        "ПУШ · ГРУДЬ И ПЛЕЧИ", "PUSH PROTOCOL"
    ),
    PULL(
        setOf(BodyPart.PULL_BACK, BodyPart.PULL_BICEPS),
        "ПУЛ · СПИНА И БИЦЕПС", "PULL PROTOCOL"
    ),
    LEGS(
        setOf(BodyPart.LEGS_QUADS, BodyPart.LEGS_HAMSTRINGS, BodyPart.LEGS_GLUTES, BodyPart.LEGS_CALVES),
        "НОГИ · ФУНДАМЕНТ", "LEG PROTOCOL"
    ),
    FULL(
        setOf(
            BodyPart.PUSH_CHEST, BodyPart.PULL_BACK, BodyPart.LEGS_QUADS,
            BodyPart.CORE_ABS
        ),
        "ФУЛ-БОДИ · ПОЛНЫЙ ЦИКЛ", "FULL PROTOCOL"
    ),
    COND(
        setOf(BodyPart.CARDIO, BodyPart.CORE_ABS),
        "КОНДИЦИЯ · ВЫНОСЛИВОСТЬ", "CONDITIONING"
    )
}

/**
 * Подбирает день (Push/Pull/Legs/Full/Cond) под строку из SPLITS.
 * Строки прототипа — "Push H", "Pull L", "Full A", "Cond", "Push" — покрываем все.
 */
private fun parseDayType(splitName: String): DayType {
    val up = splitName.uppercase()
    return when {
        "PUSH" in up -> DayType.PUSH
        "PULL" in up -> DayType.PULL
        "LEG" in up -> DayType.LEGS
        "COND" in up || "CARD" in up -> DayType.COND
        "UPPER" in up -> DayType.PUSH   // approximation — верх = чаще push-акцент
        else -> DayType.FULL
    }
}

/**
 * Главный генератор. Без AI — чистая детерминированная логика по таблицам.
 * v0.9.0: без хитрой ротации/вариативности. В следующих версиях докрутим:
 *   - chose-from-variant-pool чтобы не видеть одно и то же 6 дней подряд
 *   - progressive overload trackers
 *   - hero-specific prescribing rules (Kratos = 3×5@heavy, Dante = 4×12@moderate)
 */
object WorkoutGenerator {

    /** Сгенерить день тренировки под текущий state юзера. */
    fun forToday(state: UserState): WorkoutDay? {
        val build = state.build ?: return null
        val profile = state.profile ?: return null
        val hero = state.hero ?: return null

        val splitEntry = getTodayWorkout(state.programStartEpochMs, build)
        val dayType = parseDayType(splitEntry.name)
        val cycle = getCurrentMicrocycle(state.programStartEpochMs)
        val pool = poolFor(dayType, profile.equipment, profile.injuries)

        // Определяем сколько упражнений влезает в timeBudget (минуты)
        // Грубо: ~8 минут на упражнение (3 sets × 45с работа + 60с отдых × 3 ≈ 7-8 мин)
        val timeBudget = profile.timePerSessionMinutes.coerceAtLeast(15)
        val targetCount = (timeBudget / 8).coerceIn(3, 6)

        val selected = selectExercises(pool, dayType, targetCount)

        val blocks = selected.map { ex ->
            buildBlock(ex, build, cycle, state.baseline)
        }

        return WorkoutDay(
            title = dayType.title,
            subtitle = dayType.subtitle,
            estimatedMinutes = blocks.size * 8,  // грубо
            blocks = blocks,
            mantra = build.philosophy
        )
    }

    private fun poolFor(
        day: DayType,
        equipment: com.herotraining.data.model.EquipmentKind,
        injuries: Set<Injury>
    ): List<Exercise> {
        val available = ExerciseLibrary.available(equipment, injuries)
        return available.filter { ex ->
            ex.primary in day.parts || ex.secondary.any { it in day.parts }
        }
    }

    /**
     * Выбирает [target] упражнений из пула. Простая стратегия:
     *   - сортируем по difficulty (beginner первыми — разогрев)
     *   - затем по тому сколько основных мышц дня покрывает primary
     *   - берём первые N уникальных primary
     *   - добавляем 1 core/cardio finisher если день не CORE/CARDIO сам
     *
     * В будущем: рандомизация с seed = dayIdx, чтоб каждый день был разный
     * но воспроизводимый; anti-repeat оконно по неделе.
     */
    private fun selectExercises(pool: List<Exercise>, day: DayType, target: Int): List<Exercise> {
        if (pool.isEmpty()) return emptyList()

        // Группируем по primary, берём лучшее из каждой группы
        val byPrimary = pool.groupBy { it.primary }
        val picked = mutableListOf<Exercise>()
        val usedPrimary = mutableSetOf<BodyPart>()

        // 1) По одному упражнению на каждую primary-группу дня
        for (part in day.parts) {
            val candidates = byPrimary[part].orEmpty()
            val pick = candidates.firstOrNull { it.primary !in usedPrimary }
            if (pick != null) {
                picked += pick
                usedPrimary += pick.primary
            }
            if (picked.size >= target) break
        }

        // 2) Добираем до target — берём любые оставшиеся, не повторяя exercise id
        val usedIds = picked.map { it.id }.toMutableSet()
        for (ex in pool) {
            if (picked.size >= target) break
            if (ex.id !in usedIds) {
                picked += ex
                usedIds += ex.id
            }
        }

        // 3) Добавим core-финишер если день — не CORE/CARDIO
        if (day != DayType.COND && day != DayType.FULL) {
            val core = ExerciseLibrary.ALL.firstOrNull {
                it.id == "plank" || it.id == "leg_raise_lying" || it.id == "crunch"
            }
            if (core != null && core.id !in usedIds) picked += core
        }

        return picked.take(target + 1)   // +1 на финишер
    }

    private fun buildBlock(
        ex: Exercise,
        build: HeroBuild,
        cycle: Microcycle,
        baseline: Baseline?
    ): ExerciseBlock {
        val sets = 3

        val prescription = when (ex.unit) {
            ExerciseUnit.REPS -> repsRange(ex, build, cycle, baseline)
            ExerciseUnit.TIME_SEC -> timeSec(ex, cycle)
            ExerciseUnit.TIME_MIN -> "${cycle.restSec / 10 + 10} мин"
            ExerciseUnit.DISTANCE_M -> "200 м"
        }

        return ExerciseBlock(
            exercise = ex,
            sets = sets,
            prescription = prescription,
            restSec = cycle.restSec
        )
    }

    /** "8-12" или скейл от baseline'а если id известен. */
    private fun repsRange(ex: Exercise, build: HeroBuild, cycle: Microcycle, baseline: Baseline?): String {
        val baselineReps = baseline?.let {
            when (ex.baselineId) {
                "pushups" -> it.pushups
                "squats" -> it.squats
                "pullups" -> it.pullups
                "burpees" -> it.burpees
                else -> null
            }
        } ?: 0
        if (baselineReps <= 0) return cycle.repRange

        val mult = build.intensityMultiplier * cycle.volumeMod
        val target = (baselineReps * mult * 0.6).toInt().coerceAtLeast(3)
        return "$target"
    }

    private fun timeSec(ex: Exercise, cycle: Microcycle): String {
        // Planks / holds: 30-60 sec в зависимости от фазы
        val sec = (30 * cycle.volumeMod).toInt().coerceIn(20, 90)
        return "$sec с"
    }
}
