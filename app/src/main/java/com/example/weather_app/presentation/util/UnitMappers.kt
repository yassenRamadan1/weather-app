package com.example.weather_app.presentation.util

import com.example.weather_app.R
import com.example.weather_app.domain.entity.user.TemperatureUnit
import com.example.weather_app.domain.entity.user.WindSpeedUnit
import java.util.Locale

fun getLocalizedCountryName(countryCode: String): String {
    if (countryCode.isBlank()) return "Invalid code"
    val countryLocale = Locale.Builder().setRegion(countryCode.uppercase()).build()
    return countryLocale.displayCountry
}

fun getLocalizedCountryName(countryCode: String, targetLocale: Locale): String {
    if (countryCode.isBlank()) return "Invalid code"
    val countryLocale = Locale.Builder().setRegion(countryCode.uppercase()).build()
    return countryLocale.getDisplayCountry(targetLocale)
}

fun TemperatureUnit.toUnitRes() = when (this) {
    TemperatureUnit.CELSIUS -> R.string.unit_celsius
    TemperatureUnit.FAHRENHEIT -> R.string.unit_fahrenheit
    TemperatureUnit.KELVIN -> R.string.unit_kelvin
}

fun WindSpeedUnit.toUnitRes() = when (this) {
    WindSpeedUnit.METER_PER_SEC -> R.string.unit_meters_per_sec
    WindSpeedUnit.MILES_PER_HOUR -> R.string.unit_miles_per_hour
}
