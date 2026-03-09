package com.example.weather_app.domain.repository

import com.example.weather_app.domain.entity.AppLanguage
import com.example.weather_app.domain.entity.AppTheme
import com.example.weather_app.domain.entity.LocationMode
import com.example.weather_app.domain.entity.LocationResult
import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.entity.UserPreferences
import com.example.weather_app.domain.entity.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateLanguage(language: AppLanguage)
    suspend fun updateTemperatureUnit(unit: TemperatureUnit)
    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit)
    suspend fun getPreferredLocation(savedPreferences: UserPreferences): LocationResult
    suspend fun updateLocationMode(mode: LocationMode)
    suspend fun updateSavedLocation(lat: Double, lon: Double)
}