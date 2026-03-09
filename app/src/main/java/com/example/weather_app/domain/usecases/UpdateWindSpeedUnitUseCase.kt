package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.WindSpeedUnit
import com.example.weather_app.domain.repository.UserPreferencesRepository

class UpdateWindSpeedUnitUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(unit: WindSpeedUnit) =
        userPreferencesRepository.updateWindSpeedUnit(unit)
}