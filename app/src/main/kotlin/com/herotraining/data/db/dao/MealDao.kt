package com.herotraining.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.herotraining.data.db.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals WHERE day = :day ORDER BY loggedAt ASC")
    fun observeForDay(day: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE day = :day ORDER BY loggedAt ASC")
    suspend fun getForDay(day: String): List<MealEntity>

    @Insert
    suspend fun insert(meal: MealEntity): Long

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("DELETE FROM meals WHERE day != :keepDay")
    suspend fun pruneOld(keepDay: String)
}
