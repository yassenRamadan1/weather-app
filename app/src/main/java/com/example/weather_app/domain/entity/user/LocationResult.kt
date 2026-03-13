package com.example.weather_app.domain.entity.user

sealed class LocationResult {

    data class Success(
        val lat: Double,
        val lon: Double,
        val cityName: String? = null,
        val source: LocationSource = LocationSource.GPS,
        val isStale: Boolean = false
    ) : LocationResult()
    data object NeedPermission : LocationResult()
    data object GpsDisabled : LocationResult()
    data object GpsNoFix : LocationResult()
    data object NoSavedLocation : LocationResult()
    data class Error(val cause: Throwable) : LocationResult()
}

enum class LocationSource { GPS, LAST_KNOWN, SAVED_PREFERENCES }