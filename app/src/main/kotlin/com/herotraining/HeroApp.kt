package com.herotraining

import android.app.Application
import androidx.work.Configuration
import com.herotraining.data.db.AppDatabase
import com.herotraining.data.repo.StateRepository

class HeroApp : Application(), Configuration.Provider {

    lateinit var database: AppDatabase
        private set

    lateinit var stateRepository: StateRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.get(this)
        stateRepository = StateRepository(database)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
