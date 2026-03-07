package com.example.weather_app.domain.datasource

import com.example.weather_app.domain.entity.AppLanguage
import com.example.weather_app.domain.entity.AppTheme
import com.example.weather_app.domain.entity.LocationMode
import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.entity.UserPreferences
import com.example.weather_app.domain.entity.WindSpeedUnit
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