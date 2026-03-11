package com.example.weather_app.data.user.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.weather_app.data.user.local.UserPreferencesDataSource
import com.example.weather_app.domain.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesDataSourceImpl(
    private val dataStore: DataStore<Preferences>
): UserPreferencesDataSource {
    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            temperatureUnit = TemperatureUnit.entries.find {
                it.name == prefs[PreferencesKeys.TEMPERATURE_UNIT]
            } ?: TemperatureUnit.CELSIUS,
            windSpeedUnit = WindSpeedUnit.entries.find {
                it.name == prefs[PreferencesKeys.WIND_SPEED_UNIT]
            } ?: WindSpeedUnit.METER_PER_SEC,
            language = AppLanguage.entries.find {
                it.name == prefs[PreferencesKeys.LANGUAGE]
            } ?: AppLanguage.ENGLISH,
            theme = AppTheme.entries.find {
                it.name == prefs[PreferencesKeys.THEME]
            } ?: AppTheme.SYSTEM,
            locationMode = LocationMode.entries.find {
                it.name == prefs[PreferencesKeys.LOCATION_MODE]
            } ?: LocationMode.GPS,
            savedLat = prefs[PreferencesKeys.SAVED_LAT],
            savedLon = prefs[PreferencesKeys.SAVED_LON]
        )
    }

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { it[PreferencesKeys.TEMPERATURE_UNIT] = unit.name }
    }

    override suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        dataStore.edit { it[PreferencesKeys.WIND_SPEED_UNIT] = unit.name }
    }

    override suspend fun updateLanguage(lang: AppLanguage) {
        dataStore.edit { it[PreferencesKeys.LANGUAGE] = lang.name }
    }

    override suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit { it[PreferencesKeys.THEME] = theme.name }
    }

    override suspend fun updateLocationMode(mode: LocationMode) {
        dataStore.edit { it[PreferencesKeys.LOCATION_MODE] = mode.name }
    }

    override suspend fun updateSavedLocation(lat: Double, lon: Double) {
        dataStore.edit {
            it[PreferencesKeys.SAVED_LAT] = lat
            it[PreferencesKeys.SAVED_LON] = lon
        }
    }

}