package com.herotraining.data.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.herotraining.data.db.AppDatabase
import com.herotraining.data.db.entity.DiaryEntryEntity
import com.herotraining.data.db.entity.MeasurementEntity
import com.herotraining.data.db.entity.ProgressPhotoEntity
import com.herotraining.data.db.entity.UserStateEntity
import com.herotraining.data.db.entity.WeightEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

/**
 * Two-way sync between Room and Firestore.
 *
 * Firestore schema:
 *   /users/{uid}
 *     - state: Map (flattened UserStateEntity)
 *     - gear: List<String>
 *     - measurements: List<Map>
 *     - weights: List<Map>
 *     - photos: List<Map>
 *     - diary: List<Map>
 *     - updatedAt: Long (epoch-ms)
 */
class FirestoreSync(private val db: AppDatabase) {
    private val fs: FirebaseFirestore = Firebase.firestore

    /** Push full local snapshot to Firestore. Called after local mutations. */
    suspend fun pushAll(uid: String) {
        val state = db.stateDao().get() ?: return
        val gear = db.gearDao().getAll()
        val doc = mapOf(
            "state" to stateToMap(state),
            "gear" to gear,
            "measurements" to measurementsAsList(),
            "weights" to weightsAsList(),
            "photos" to photosAsList(),
            "diary" to diaryAsList(),
            "updatedAt" to System.currentTimeMillis()
        )
        fs.collection("users").document(uid).set(doc).await()
    }

    /** Hard-delete the user's cloud document so a full reset actually wipes cloud too. */
    suspend fun deleteUserDoc(uid: String) {
        runCatching { fs.collection("users").document(uid).delete().await() }
    }

    /** Pull from Firestore and replace local data (for reinstalls / new device sign-in). */
    suspend fun pullAll(uid: String): Boolean {
        val snapshot = fs.collection("users").document(uid).get().await()
        if (!snapshot.exists()) return false

        @Suppress("UNCHECKED_CAST")
        val stateMap = snapshot.get("state") as? Map<String, Any?> ?: return false
        val gearList = (snapshot.get("gear") as? List<String>).orEmpty()
        val measurements = (snapshot.get("measurements") as? List<Map<String, Any?>>).orEmpty()
        val weights = (snapshot.get("weights") as? List<Map<String, Any?>>).orEmpty()
        val photos = (snapshot.get("photos") as? List<Map<String, Any?>>).orEmpty()
        val diary = (snapshot.get("diary") as? List<Map<String, Any?>>).orEmpty()

        // Replace tables
        db.stateDao().upsert(mapToState(stateMap))
        db.gearDao().setAll(gearList)
        // Simple replace strategy for list-tables: insert all (ids will be regenerated)
        measurements.forEach { m ->
            db.measurementDao().insert(
                MeasurementEntity(
                    date = m["date"] as? String ?: "",
                    chestCm = (m["chestCm"] as? Number)?.toDouble(),
                    waistCm = (m["waistCm"] as? Number)?.toDouble(),
                    hipsCm = (m["hipsCm"] as? Number)?.toDouble(),
                    bicepCm = (m["bicepCm"] as? Number)?.toDouble(),
                    thighCm = (m["thighCm"] as? Number)?.toDouble(),
                    neckCm = (m["neckCm"] as? Number)?.toDouble(),
                    recordedAt = (m["recordedAt"] as? Number)?.toLong() ?: 0L
                )
            )
        }
        weights.forEach { w ->
            db.weightDao().upsert(
                WeightEntryEntity(
                    date = w["date"] as? String ?: "",
                    weightKg = (w["weightKg"] as? Number)?.toDouble() ?: 0.0,
                    recordedAt = (w["recordedAt"] as? Number)?.toLong() ?: 0L
                )
            )
        }
        photos.forEach { p ->
            db.photoDao().insert(
                ProgressPhotoEntity(
                    date = p["date"] as? String ?: "",
                    localUri = p["localUri"] as? String ?: "",
                    pose = p["pose"] as? String,
                    note = p["note"] as? String,
                    recordedAt = (p["recordedAt"] as? Number)?.toLong() ?: 0L
                )
            )
        }
        diary.forEach { e ->
            db.diaryDao().insert(
                DiaryEntryEntity(
                    date = e["date"] as? String ?: "",
                    mood = (e["mood"] as? Number)?.toInt(),
                    energy = (e["energy"] as? Number)?.toInt(),
                    text = e["text"] as? String ?: "",
                    recordedAt = (e["recordedAt"] as? Number)?.toLong() ?: 0L
                )
            )
        }
        return true
    }

    // ---- Helpers (local queries → list of maps) ----

    private suspend fun measurementsAsList(): List<Map<String, Any?>> {
        val list = db.measurementDao().observeAll().first()
        return list.map { m ->
            mapOf(
                "date" to m.date,
                "chestCm" to m.chestCm, "waistCm" to m.waistCm, "hipsCm" to m.hipsCm,
                "bicepCm" to m.bicepCm, "thighCm" to m.thighCm, "neckCm" to m.neckCm,
                "recordedAt" to m.recordedAt
            )
        }
    }

    private suspend fun weightsAsList(): List<Map<String, Any?>> {
        val list = db.weightDao().observeAll().first()
        return list.map { w -> mapOf("date" to w.date, "weightKg" to w.weightKg, "recordedAt" to w.recordedAt) }
    }

    private suspend fun photosAsList(): List<Map<String, Any?>> {
        val list = db.photoDao().observeAll().first()
        return list.map { p ->
            mapOf(
                "date" to p.date, "localUri" to p.localUri,
                "pose" to p.pose, "note" to p.note, "recordedAt" to p.recordedAt
            )
        }
    }

    private suspend fun diaryAsList(): List<Map<String, Any?>> {
        val list = db.diaryDao().observeAll().first()
        return list.map { e ->
            mapOf(
                "date" to e.date, "mood" to e.mood, "energy" to e.energy,
                "text" to e.text, "recordedAt" to e.recordedAt
            )
        }
    }

    private fun stateToMap(s: UserStateEntity): Map<String, Any?> = mapOf(
        "onboarded" to s.onboarded,
        "heroId" to s.heroId, "buildId" to s.buildId,
        "age" to s.age, "weight" to s.weight, "height" to s.height, "sex" to s.sex,
        "experience" to s.experience, "equipment" to s.equipment, "timePerSession" to s.timePerSession,
        "injuries" to s.injuries,
        "foodStyle" to s.foodStyle, "exclusions" to s.exclusions, "goal" to s.goal,
        "mealsPerDay" to s.mealsPerDay, "keepTreats" to s.keepTreats,
        "basePushups" to s.basePushups, "baseSquats" to s.baseSquats,
        "basePlankSec" to s.basePlankSec, "basePullups" to s.basePullups,
        "baseBurpees" to s.baseBurpees, "baseCardioMinutes" to s.baseCardioMinutes,
        "baseFlexibility" to s.baseFlexibility,
        "programStartDate" to s.programStartDate,
        "streak" to s.streak, "lastCheckin" to s.lastCheckin,
        "coins" to s.coins, "xp" to s.xp, "rankPoints" to s.rankPoints, "combo" to s.combo,
        "lastMealTime" to s.lastMealTime, "lastComboUpdate" to s.lastComboUpdate,
        "todayTrainingDone" to s.todayTrainingDone,
        "todayNutritionDone" to s.todayNutritionDone,
        "todayBonusDone" to s.todayBonusDone
    )

    private fun mapToState(m: Map<String, Any?>): UserStateEntity = UserStateEntity(
        id = 0,
        onboarded = m["onboarded"] as? Boolean ?: false,
        heroId = m["heroId"] as? String,
        buildId = m["buildId"] as? String,
        age = (m["age"] as? Number)?.toInt(),
        weight = (m["weight"] as? Number)?.toInt(),
        height = (m["height"] as? Number)?.toInt(),
        sex = m["sex"] as? String,
        experience = m["experience"] as? String,
        equipment = m["equipment"] as? String,
        timePerSession = (m["timePerSession"] as? Number)?.toInt(),
        injuries = m["injuries"] as? String ?: "",
        foodStyle = m["foodStyle"] as? String,
        exclusions = m["exclusions"] as? String ?: "",
        goal = m["goal"] as? String,
        mealsPerDay = (m["mealsPerDay"] as? Number)?.toInt(),
        keepTreats = m["keepTreats"] as? String ?: "",
        basePushups = (m["basePushups"] as? Number)?.toInt() ?: 0,
        baseSquats = (m["baseSquats"] as? Number)?.toInt() ?: 0,
        basePlankSec = (m["basePlankSec"] as? Number)?.toInt() ?: 0,
        basePullups = (m["basePullups"] as? Number)?.toInt() ?: 0,
        baseBurpees = (m["baseBurpees"] as? Number)?.toInt() ?: 0,
        baseCardioMinutes = (m["baseCardioMinutes"] as? Number)?.toInt() ?: 0,
        baseFlexibility = (m["baseFlexibility"] as? Number)?.toInt() ?: 0,
        programStartDate = (m["programStartDate"] as? Number)?.toLong(),
        streak = (m["streak"] as? Number)?.toInt() ?: 0,
        lastCheckin = m["lastCheckin"] as? String,
        coins = (m["coins"] as? Number)?.toInt() ?: 0,
        xp = (m["xp"] as? Number)?.toInt() ?: 0,
        rankPoints = (m["rankPoints"] as? Number)?.toInt() ?: 0,
        combo = (m["combo"] as? Number)?.toInt() ?: 0,
        lastMealTime = (m["lastMealTime"] as? Number)?.toLong(),
        lastComboUpdate = (m["lastComboUpdate"] as? Number)?.toLong(),
        todayTrainingDone = m["todayTrainingDone"] as? Boolean ?: false,
        todayNutritionDone = m["todayNutritionDone"] as? Boolean ?: false,
        todayBonusDone = m["todayBonusDone"] as? Boolean ?: false
    )
}
