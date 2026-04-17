package com.herotraining.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.herotraining.data.db.dao.AchievementDao
import com.herotraining.data.db.dao.AuthDao
import com.herotraining.data.db.dao.DiaryDao
import com.herotraining.data.db.dao.GearDao
import com.herotraining.data.db.dao.MealDao
import com.herotraining.data.db.dao.MeasurementDao
import com.herotraining.data.db.dao.ProgressPhotoDao
import com.herotraining.data.db.dao.StateDao
import com.herotraining.data.db.dao.WeightDao
import com.herotraining.data.db.entity.AchievementEntity
import com.herotraining.data.db.entity.AuthEntity
import com.herotraining.data.db.entity.DiaryEntryEntity
import com.herotraining.data.db.entity.GearEntity
import com.herotraining.data.db.entity.MealEntity
import com.herotraining.data.db.entity.MeasurementEntity
import com.herotraining.data.db.entity.ProgressPhotoEntity
import com.herotraining.data.db.entity.UserStateEntity
import com.herotraining.data.db.entity.WeightEntryEntity

@Database(
    entities = [
        UserStateEntity::class,
        MealEntity::class,
        GearEntity::class,
        AchievementEntity::class,
        MeasurementEntity::class,
        WeightEntryEntity::class,
        ProgressPhotoEntity::class,
        DiaryEntryEntity::class,
        AuthEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stateDao(): StateDao
    abstract fun mealDao(): MealDao
    abstract fun gearDao(): GearDao
    abstract fun achievementDao(): AchievementDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun weightDao(): WeightDao
    abstract fun photoDao(): ProgressPhotoDao
    abstract fun diaryDao(): DiaryDao
    abstract fun authDao(): AuthDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "hero_training.db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}
