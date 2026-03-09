package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.repository.UserPreferencesRepository

class UpdateSavedLocationUseCase(
    private val locationRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double) {
        locationRepository.updateSavedLocation(lat, lon)
    }
}