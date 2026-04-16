package com.herotraining.data.repo

import com.herotraining.data.catalog.HeroCatalog
import com.herotraining.data.db.AppDatabase
import com.herotraining.data.db.entity.AchievementEntity
import com.herotraining.data.db.entity.GearEntity
import com.herotraining.data.db.entity.MealEntity
import com.herotraining.data.db.entity.UserStateEntity
import com.herotraining.data.model.Baseline
import com.herotraining.data.model.DEFAULT_USER_STATE
import com.herotraining.data.model.Exclusion
import com.herotraining.data.model.EquipmentKind
import com.herotraining.data.model.Experience
import com.herotraining.data.model.FoodStyle
import com.herotraining.data.model.Gender
import com.herotraining.data.model.Injury
import com.herotraining.data.model.LoggedMeal
import com.herotraining.data.model.NutritionGoal
import com.herotraining.data.model.NutritionProfile
import com.herotraining.data.model.Profile
import com.herotraining.data.model.TreatKind
import com.herotraining.data.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Today's date as yyyy-MM-dd (system default zone). */
fun today(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

class StateRepository(private val db: AppDatabase) {

    fun observeState(): Flow<UserState> =
        combine(
            db.stateDao().observe(),
            db.mealDao().observeForDay(today()),
            db.gearDao().observeAll(),
            db.achievementDao().observeAll()
        ) { state, meals, gear, achievements ->
            materialize(state, meals, gear, achievements.map { it.code })
        }

    suspend fun snapshot(): UserState {
        val state = db.stateDao().get()
        val meals = db.mealDao().getForDay(today())
        val gear = db.gearDao().getAll()
        // Achievements aren't needed for most snapshots
        return materialize(state, meals, gear, emptyList())
    }

    private fun materialize(
        state: UserStateEntity?,
        meals: List<MealEntity>,
        gear: List<String>,
        achievements: List<String>
    ): UserState {
        if (state == null) return DEFAULT_USER_STATE.copy(gear = gear.toSet())

        val hero = state.heroId?.let { HeroCatalog.byId(it) }
        val build = hero?.builds?.firstOrNull { it.id == state.buildId }
        val profile = if (state.age != null && state.weight != null && state.height != null && state.sex != null
            && state.experience != null && state.equipment != null && state.timePerSession != null) {
            Profile(
                age = state.age, weight = state.weight, height = state.height,
                sex = Gender.fromKey(state.sex) ?: Gender.MALE,
                experience = Experience.fromKey(state.experience) ?: Experience.NONE,
                equipment = EquipmentKind.fromKey(state.equipment) ?: EquipmentKind.BODYWEIGHT,
                timePerSessionMinutes = state.timePerSession,
                injuries = state.injuries.split(",").filter { it.isNotEmpty() }
                    .mapNotNull { Injury.fromKey(it) }.toSet()
            )
        } else null

        val nutrition = if (state.foodStyle != null && state.goal != null && state.mealsPerDay != null) {
            NutritionProfile(
                style = FoodStyle.fromKey(state.foodStyle) ?: FoodStyle.OMNIVORE,
                exclusions = state.exclusions.split(",").filter { it.isNotEmpty() }
                    .mapNotNull { Exclusion.fromKey(it) }.toSet(),
                goal = NutritionGoal.fromKey(state.goal) ?: NutritionGoal.MAINTAIN,
                mealsPerDay = state.mealsPerDay,
                keepTreats = state.keepTreats.split(",").filter { it.isNotEmpty() }
                    .mapNotNull { TreatKind.fromKey(it) }.toSet()
            )
        } else null

        val baseline = Baseline(
            pushups = state.basePushups,
            squats = state.baseSquats,
            plankSec = state.basePlankSec,
            pullups = state.basePullups,
            burpees = state.baseBurpees,
            cardioMinutes = state.baseCardioMinutes,
            flexibilityScale = state.baseFlexibility
        )

        return UserState(
            onboarded = state.onboarded,
            hero = hero, build = build, profile = profile,
            nutrition = nutrition, baseline = baseline,
            gear = gear.toSet(),
            programStartEpochMs = state.programStartDate,
            streak = state.streak,
            lastCheckin = state.lastCheckin,
            coins = state.coins, xp = state.xp, rankPoints = state.rankPoints,
            combo = state.combo,
            lastMealTime = state.lastMealTime,
            lastComboUpdate = state.lastComboUpdate,
            todayTrainingDone = state.todayTrainingDone,
            todayNutritionDone = state.todayNutritionDone,
            todayBonusDone = state.todayBonusDone,
            todayMeals = meals.map { m ->
                LoggedMeal(m.id, m.text, m.kcal, m.portion, m.untracked, m.time)
            },
            achievements = achievements.toSet()
        )
    }

    suspend fun completeOnboarding(
        heroId: String, buildId: String,
        profile: Profile, nutrition: NutritionProfile, baseline: Baseline, gear: Set<String>
    ) {
        val now = System.currentTimeMillis()
        db.stateDao().upsert(
            UserStateEntity(
                id = 0,
                onboarded = true,
                heroId = heroId,
                buildId = buildId,
                age = profile.age, weight = profile.weight, height = profile.height,
                sex = profile.sex.key, experience = profile.experience.key,
                equipment = profile.equipment.key, timePerSession = profile.timePerSessionMinutes,
                injuries = profile.injuries.joinToString(",") { it.key },
                foodStyle = nutrition.style.key,
                exclusions = nutrition.exclusions.joinToString(",") { it.key },
                goal = nutrition.goal.key,
                mealsPerDay = nutrition.mealsPerDay,
                keepTreats = nutrition.keepTreats.joinToString(",") { it.key },
                basePushups = baseline.pushups, baseSquats = baseline.squats,
                basePlankSec = baseline.plankSec, basePullups = baseline.pullups,
                baseBurpees = baseline.burpees, baseCardioMinutes = baseline.cardioMinutes,
                baseFlexibility = baseline.flexibilityScale,
                programStartDate = now, combo = 10, lastComboUpdate = now
            )
        )
        db.gearDao().setAll(gear)
    }

    suspend fun markQuest(type: QuestType, rpBase: Int, comboDelta: Int, comboMultiplier: Double): MarkResult {
        val s = db.stateDao().get() ?: return MarkResult.Empty
        val today = today()
        val xpBonus = (rpBase * comboMultiplier * 3).toInt()
        val coinsBonus = (rpBase * comboMultiplier).toInt()
        val newCombo = (s.combo + comboDelta).coerceIn(0, 100)

        val base = s.copy(
            todayTrainingDone = if (type == QuestType.TRAINING) true else s.todayTrainingDone,
            todayNutritionDone = if (type == QuestType.NUTRITION) true else s.todayNutritionDone,
            todayBonusDone = if (type == QuestType.BONUS) true else s.todayBonusDone,
            xp = s.xp + xpBonus,
            coins = s.coins + coinsBonus,
            rankPoints = s.rankPoints + rpBase,
            combo = newCombo,
            lastComboUpdate = System.currentTimeMillis()
        )

        val allDone = base.todayTrainingDone && base.todayNutritionDone && base.todayBonusDone
        val completedToday = allDone && s.lastCheckin != today

        val final = if (completedToday) {
            base.copy(
                streak = s.streak + 1,
                lastCheckin = today,
                coins = base.coins + 25,
                xp = base.xp + 50,
                rankPoints = base.rankPoints + 10,
                combo = (base.combo + 10).coerceIn(0, 100)
            )
        } else base

        db.stateDao().upsert(final)

        if (completedToday) {
            val codes = listOf(3 to "streak_3", 7 to "streak_7", 30 to "streak_30")
            for ((th, code) in codes) {
                if (final.streak == th && db.achievementDao().has(code) == 0) {
                    db.achievementDao().insert(AchievementEntity(code, System.currentTimeMillis()))
                }
            }
        }
        return MarkResult(gainXp = xpBonus + if (completedToday) 50 else 0, completedToday = completedToday)
    }

    suspend fun addMeal(text: String, kcal: Int, portion: String?, untracked: Boolean, comboDelta: Int) {
        val now = System.currentTimeMillis()
        val time = java.time.LocalTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm"))
        db.mealDao().insert(
            MealEntity(
                day = today(), time = time, text = text, kcal = kcal,
                portion = portion, untracked = untracked, loggedAt = now
            )
        )
        val s = db.stateDao().get() ?: return
        db.stateDao().upsert(
            s.copy(
                lastMealTime = now,
                combo = (s.combo + comboDelta).coerceIn(0, 100),
                lastComboUpdate = now
            )
        )
    }

    suspend fun deleteMeal(meal: LoggedMeal) {
        // Reconstruct MealEntity for @Delete; easier: pass through id to a dedicated DAO call if needed.
        // Current DAO uses @Delete on entity — so we need the entity back:
        val forDay = db.mealDao().getForDay(today())
        forDay.firstOrNull { it.id == meal.id }?.let { db.mealDao().delete(it) }
    }

    suspend fun setGear(gear: Set<String>) {
        db.gearDao().setAll(gear)
    }

    /** Rolls today's quest flags/meals if a new day has started and we had progress yesterday. */
    suspend fun rolloverDayIfNeeded() {
        val s = db.stateDao().get() ?: return
        val today = today()
        if (s.lastCheckin != null && s.lastCheckin != today &&
            (s.todayTrainingDone || s.todayNutritionDone || s.todayBonusDone)) {
            val daysDiff = LocalDate.parse(s.lastCheckin).let {
                LocalDate.now().toEpochDay() - it.toEpochDay()
            }
            db.stateDao().upsert(
                s.copy(
                    todayTrainingDone = false,
                    todayNutritionDone = false,
                    todayBonusDone = false,
                    streak = if (daysDiff > 1) 0 else s.streak,
                    combo = (s.combo - if (daysDiff > 1) 25 else 0).coerceAtLeast(0)
                )
            )
            db.mealDao().pruneOld(today)
        }
    }

    suspend fun reset() {
        db.stateDao().clear()
        db.mealDao().pruneOld("")
        db.gearDao().clear()
    }
}

enum class QuestType { TRAINING, NUTRITION, BONUS }
data class MarkResult(val gainXp: Int, val completedToday: Boolean) {
    companion object { val Empty = MarkResult(0, false) }
}
