package com.herotraining.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.herotraining.data.db.entity.GearEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GearDao {
    @Query("SELECT gearId FROM gear")
    fun observeAll(): Flow<List<String>>

    @Query("SELECT gearId FROM gear")
    suspend fun getAll(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gear: GearEntity)

    @Query("DELETE FROM gear WHERE gearId = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM gear")
    suspend fun clear()

    @Transaction
    suspend fun setAll(ids: Collection<String>) {
        clear()
        ids.forEach { insert(GearEntity(it)) }
    }
}
