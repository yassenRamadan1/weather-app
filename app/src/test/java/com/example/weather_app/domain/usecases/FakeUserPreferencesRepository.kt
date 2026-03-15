package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.user.*
import com.example.weather_app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val _userPreferences = MutableStateFlow(UserPreferences())
    override val userPreferences: Flow<UserPreferences> = _userPreferences

    var locationResult: LocationResult = LocationResult.NoSavedLocation

    override suspend fun updateTheme(theme: AppTheme) {
        _userPreferences.value = _userPreferences.value.copy(theme = theme)
    }

    override suspend fun updateLanguage(language: AppLanguage) {
        _userPreferences.value = _userPreferences.value.copy(language = language)
    }

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        _userPreferences.value = _userPreferences.value.copy(temperatureUnit = unit)
    }

    override suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        _userPreferences.value = _userPreferences.value.copy(windSpeedUnit = unit)
    }

    override suspend fun updateLocationMode(mode: LocationMode) {
        _userPreferences.value = _userPreferences.value.copy(locationMode = mode)
    }

    override suspend fun getPreferredLocation(savedPreferences: UserPreferences): LocationResult {
        return locationResult
    }

    override suspend fun updateSavedLocation(lat: Double, lon: Double) {
        _userPreferences.value = _userPreferences.value.copy(savedLat = lat, savedLon = lon)
    }
}
