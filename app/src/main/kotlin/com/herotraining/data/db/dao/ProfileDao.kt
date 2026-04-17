package com.herotraining.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.herotraining.data.db.entity.AuthEntity
import com.herotraining.data.db.entity.DiaryEntryEntity
import com.herotraining.data.db.entity.MeasurementEntity
import com.herotraining.data.db.entity.ProgressPhotoEntity
import com.herotraining.data.db.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurements ORDER BY recordedAt DESC")
    fun observeAll(): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements ORDER BY recordedAt DESC LIMIT 1")
    fun observeLatest(): Flow<MeasurementEntity?>

    @Insert
    suspend fun insert(m: MeasurementEntity): Long

    @Delete
    suspend fun delete(m: MeasurementEntity)
}

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_log ORDER BY recordedAt DESC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_log WHERE date = :day LIMIT 1")
    suspend fun getForDay(day: String): WeightEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(w: WeightEntryEntity): Long

    @Delete
    suspend fun delete(w: WeightEntryEntity)
}

@Dao
interface ProgressPhotoDao {
    @Query("SELECT * FROM progress_photos ORDER BY recordedAt DESC")
    fun observeAll(): Flow<List<ProgressPhotoEntity>>

    @Insert
    suspend fun insert(p: ProgressPhotoEntity): Long

    @Delete
    suspend fun delete(p: ProgressPhotoEntity)
}

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries ORDER BY recordedAt DESC")
    fun observeAll(): Flow<List<DiaryEntryEntity>>

    @Query("SELECT MAX(recordedAt) FROM diary_entries")
    suspend fun lastRecordedAt(): Long?

    @Insert
    suspend fun insert(e: DiaryEntryEntity): Long

    @Delete
    suspend fun delete(e: DiaryEntryEntity)
}

@Dao
interface AuthDao {
    @Query("SELECT * FROM auth WHERE id = 0 LIMIT 1")
    fun observe(): Flow<AuthEntity?>

    @Query("SELECT * FROM auth WHERE id = 0 LIMIT 1")
    suspend fun get(): AuthEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(a: AuthEntity)

    @Query("DELETE FROM auth")
    suspend fun clear()
}
