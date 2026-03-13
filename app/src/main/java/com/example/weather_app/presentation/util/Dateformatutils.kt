package com.example.weather_app.presentation.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

val Locale.isArabic: Boolean
    get() = language == "ar"

fun getLocalizedDayName(
    timestamp: Long,
    locale: Locale,
    zone: ZoneId = ZoneId.systemDefault(),
): String {
    val localDate = Instant.ofEpochSecond(timestamp)
        .atZone(zone)
        .toLocalDate()
    val textStyle = if (locale.isArabic) TextStyle.FULL else TextStyle.SHORT
    return localDate.dayOfWeek.getDisplayName(textStyle, locale)
}

fun getLocalizedTime(
    timestamp: Long,
    locale: Locale,
    zone: ZoneId = ZoneId.systemDefault(),
    forceWesternDigits: Boolean = true,
): String {
    val effectiveLocale = if (forceWesternDigits && locale.isArabic) {
        Locale.Builder()
            .setLocale(locale)
            .setUnicodeLocaleKeyword("nu", "latn")
            .build()
    } else {
        locale
    }
    return Instant.ofEpochSecond(timestamp)
        .atZone(zone)
        .format(DateTimeFormatter.ofPattern("HH:mm", effectiveLocale))
}