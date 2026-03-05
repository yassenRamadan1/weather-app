package com.example.weather_app.domain.entity


data class UserPreferences(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METER_PER_SEC,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM,
    val locationMode: LocationMode = LocationMode.GPS,
    val savedLat: Double? = null,
    val savedLon: Double? = null
)

enum class TemperatureUnit(val apiValue: String, val symbol: String) {
    CELSIUS("metric", "°C"),
    FAHRENHEIT("imperial", "°F"),
    KELVIN("standard", "K")
}

enum class WindSpeedUnit(val symbol: String) {
    METER_PER_SEC("m/s"),
    MILES_PER_HOUR("mph")
}

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    ARABIC("ar")
}

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class LocationMode {
    GPS, MAP
}