package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.UserPreferences
import com.example.weather_app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> =
        userPreferencesRepository.userPreferences
}