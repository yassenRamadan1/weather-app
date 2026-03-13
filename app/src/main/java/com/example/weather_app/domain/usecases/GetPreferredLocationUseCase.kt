package com.example.weather_app.domain.usecases

import com.example.weather_app.data.user.local.UserPreferencesDataSource
import com.example.weather_app.domain.entity.user.LocationResult
import com.example.weather_app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first

class GetPreferredLocationUseCase(
    private val locationRepository: UserPreferencesRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource
) {
    suspend operator fun invoke(): LocationResult {
        val prefs = userPreferencesDataSource.userPreferences.first()
        return locationRepository.getPreferredLocation(prefs)
    }
}