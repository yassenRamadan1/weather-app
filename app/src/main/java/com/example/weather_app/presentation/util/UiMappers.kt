package com.example.weather_app.presentation.util

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