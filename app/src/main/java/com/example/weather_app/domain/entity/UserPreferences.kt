package com.example.weather_app.domain.entity

import android.content.Context
import android.content.res.Configuration
import java.util.Locale


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
    ARABIC("ar");

    val locale: Locale get() = Locale(code)

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class LocationMode {
    GPS, MAP
}

fun Context.updateLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    val localizedContext = createConfigurationContext(config)
    return object : android.content.ContextWrapper(this) {
        override fun getResources(): android.content.res.Resources = localizedContext.resources
        override fun getAssets(): android.content.res.AssetManager = localizedContext.assets
        override fun getSystemService(name: String): Any? = localizedContext.getSystemService(name)
        override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
            return localizedContext.createConfigurationContext(overrideConfiguration)
        }
    }
}
