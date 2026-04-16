package com.herotraining.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.herotraining.data.db.entity.UserStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StateDao {
    @Query("SELECT * FROM user_state WHERE id = 0 LIMIT 1")
    fun observe(): Flow<UserStateEntity?>

    @Query("SELECT * FROM user_state WHERE id = 0 LIMIT 1")
    suspend fun get(): UserStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: UserStateEntity)

    @Query("DELETE FROM user_state")
    suspend fun clear()
}
