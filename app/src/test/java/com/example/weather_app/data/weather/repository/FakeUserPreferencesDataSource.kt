package com.example.weather_app.data.weather.repository

import com.example.weather_app.data.user.local.UserPreferencesDataSource
import com.example.weather_app.domain.entity.user.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserPreferencesDataSource : UserPreferencesDataSource {
    private val _userPreferences = MutableStateFlow(UserPreferences())
    override val userPreferences: Flow<UserPreferences> = _userPreferences

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        _userPreferences.value = _userPreferences.value.copy(temperatureUnit = unit)
    }

    override suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        _userPreferences.value = _userPreferences.value.copy(windSpeedUnit = unit)
    }

    override suspend fun updateLanguage(lang: AppLanguage) {
        _userPreferences.value = _userPreferences.value.copy(language = lang)
    }

    override suspend fun updateTheme(theme: AppTheme) {
        _userPreferences.value = _userPreferences.value.copy(theme = theme)
    }

    override suspend fun updateLocationMode(mode: LocationMode) {
        _userPreferences.value = _userPreferences.value.copy(locationMode = mode)
    }

    override suspend fun updateSavedLocation(lat: Double, lon: Double) {
        _userPreferences.value = _userPreferences.value.copy(savedLat = lat, savedLon = lon)
    }
}
