package com.herotraining

import android.app.Application
import androidx.work.Configuration
import com.herotraining.data.db.AppDatabase
import com.herotraining.data.health.HealthConnectManager
import com.herotraining.data.repo.ProfileRepository
import com.herotraining.data.repo.StateRepository

class HeroApp : Application(), Configuration.Provider {

    lateinit var database: AppDatabase
        private set

    lateinit var stateRepository: StateRepository
        private set

    lateinit var profileRepository: ProfileRepository
        private set

    lateinit var healthConnect: HealthConnectManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.get(this)
        stateRepository = StateRepository(database)
        profileRepository = ProfileRepository(database)
        healthConnect = HealthConnectManager(this)
        com.herotraining.work.NotificationChannels.ensureCreated(this)
        com.herotraining.work.WorkScheduler.scheduleAll(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
