package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.LocationMode
import com.example.weather_app.domain.repository.UserPreferencesRepository

class UpdateLocationModeUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(mode: LocationMode) =
        userPreferencesRepository.updateLocationMode(mode)
}