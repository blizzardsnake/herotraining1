package com.herotraining.ui.screens.profile_view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.herotraining.HeroApp
import com.herotraining.data.db.entity.AuthEntity
import com.herotraining.data.db.entity.DiaryEntryEntity
import com.herotraining.data.db.entity.MeasurementEntity
import com.herotraining.data.db.entity.ProgressPhotoEntity
import com.herotraining.data.db.entity.WeightEntryEntity
import com.herotraining.data.repo.ProfileRepository
import com.herotraining.data.repo.StateRepository
import com.herotraining.data.model.UserState
import com.herotraining.data.model.DEFAULT_USER_STATE
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val profile: ProfileRepository = (app as HeroApp).profileRepository
    private val state: StateRepository = (app as HeroApp).stateRepository

    val userState: StateFlow<UserState> = state.observeState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), DEFAULT_USER_STATE)

    val auth: StateFlow<AuthEntity?> = profile.observeAuth()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val measurements: StateFlow<List<MeasurementEntity>> = profile.observeMeasurements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val weights: StateFlow<List<WeightEntryEntity>> = profile.observeWeights()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val photos: StateFlow<List<ProgressPhotoEntity>> = profile.observePhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val diary: StateFlow<List<DiaryEntryEntity>> = profile.observeDiary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun addMeasurement(m: MeasurementEntity) = viewModelScope.launch { profile.addMeasurement(m) }
    fun deleteMeasurement(m: MeasurementEntity) = viewModelScope.launch { profile.deleteMeasurement(m) }

    fun logWeight(kg: Double) = viewModelScope.launch {
        val now = System.currentTimeMillis()
        profile.upsertWeight(
            WeightEntryEntity(
                date = ProfileRepository.today(),
                weightKg = kg,
                recordedAt = now
            )
        )
    }
    fun deleteWeight(w: WeightEntryEntity) = viewModelScope.launch { profile.deleteWeight(w) }

    fun addPhoto(uri: String, pose: String?, note: String?) = viewModelScope.launch {
        profile.addPhoto(
            ProgressPhotoEntity(
                date = ProfileRepository.today(),
                localUri = uri,
                pose = pose,
                note = note,
                recordedAt = System.currentTimeMillis()
            )
        )
    }
    fun deletePhoto(p: ProgressPhotoEntity) = viewModelScope.launch { profile.deletePhoto(p) }

    fun addDiary(text: String, mood: Int?, energy: Int?) = viewModelScope.launch {
        profile.addDiary(
            DiaryEntryEntity(
                date = ProfileRepository.today(),
                mood = mood,
                energy = energy,
                text = text,
                recordedAt = System.currentTimeMillis()
            )
        )
    }
    fun deleteDiary(e: DiaryEntryEntity) = viewModelScope.launch { profile.deleteDiary(e) }
}
