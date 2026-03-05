package com.example.weather_app.data.user

import com.example.weather_app.domain.entity.*
import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateTemperatureUnit(unit: TemperatureUnit)
    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit)
    suspend fun updateLanguage(lang: AppLanguage)
    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateLocationMode(mode: LocationMode)
    suspend fun updateSavedLocation(lat: Double, lon: Double)
}