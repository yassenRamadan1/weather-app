package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.repository.UserPreferencesRepository

class UpdateTemperatureUnitUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(unit: TemperatureUnit) =
        userPreferencesRepository.updateTemperatureUnit(unit)
}