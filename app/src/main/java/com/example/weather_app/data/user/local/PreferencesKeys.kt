package com.example.weather_app.data.user.local

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
    val WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
    val LANGUAGE = stringPreferencesKey("language")
    val THEME = stringPreferencesKey("theme")
    val LOCATION_MODE = stringPreferencesKey("location_mode")
    val SAVED_LAT = doublePreferencesKey("saved_lat")
    val SAVED_LON = doublePreferencesKey("saved_lon")
}