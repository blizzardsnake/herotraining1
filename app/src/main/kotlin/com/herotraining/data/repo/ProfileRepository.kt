package com.herotraining.data.repo

import com.herotraining.data.db.AppDatabase
import com.herotraining.data.db.entity.AuthEntity
import com.herotraining.data.db.entity.DiaryEntryEntity
import com.herotraining.data.db.entity.MeasurementEntity
import com.herotraining.data.db.entity.ProgressPhotoEntity
import com.herotraining.data.db.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProfileRepository(private val db: AppDatabase) {

    // ---- Auth ----
    fun observeAuth(): Flow<AuthEntity?> = db.authDao().observe()
    suspend fun setAuth(uid: String?, email: String?, name: String?, photo: String?) {
        db.authDao().upsert(
            AuthEntity(
                id = 0,
                uid = uid,
                email = email,
                displayName = name,
                photoUrl = photo,
                signedInAt = System.currentTimeMillis()
            )
        )
    }
    suspend fun clearAuth() = db.authDao().clear()

    // ---- Measurements ----
    fun observeMeasurements(): Flow<List<MeasurementEntity>> = db.measurementDao().observeAll()
    fun observeLatestMeasurement(): Flow<MeasurementEntity?> = db.measurementDao().observeLatest()
    suspend fun addMeasurement(m: MeasurementEntity) = db.measurementDao().insert(m)
    suspend fun deleteMeasurement(m: MeasurementEntity) = db.measurementDao().delete(m)

    // ---- Weight ----
    fun observeWeights(): Flow<List<WeightEntryEntity>> = db.weightDao().observeAll()
    suspend fun upsertWeight(w: WeightEntryEntity) = db.weightDao().upsert(w)
    suspend fun deleteWeight(w: WeightEntryEntity) = db.weightDao().delete(w)

    // ---- Photos ----
    fun observePhotos(): Flow<List<ProgressPhotoEntity>> = db.photoDao().observeAll()
    suspend fun addPhoto(p: ProgressPhotoEntity) = db.photoDao().insert(p)
    suspend fun deletePhoto(p: ProgressPhotoEntity) = db.photoDao().delete(p)

    // ---- Diary ----
    fun observeDiary(): Flow<List<DiaryEntryEntity>> = db.diaryDao().observeAll()
    suspend fun addDiary(e: DiaryEntryEntity) = db.diaryDao().insert(e)
    suspend fun deleteDiary(e: DiaryEntryEntity) = db.diaryDao().delete(e)

    /** Days since last check-in (measurement / weight / diary entry — whichever is latest). */
    suspend fun daysSinceLastCheckin(): Int {
        val last = db.diaryDao().lastRecordedAt() ?: 0L
        if (last == 0L) return 999
        val lastDate = LocalDate.ofEpochDay(last / 86_400_000L)
        return (LocalDate.now().toEpochDay() - lastDate.toEpochDay()).toInt()
    }

    companion object {
        fun today(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}
