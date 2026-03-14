package com.example.weather_app.domain.repository

interface GeocodingProvider {
    suspend fun reverseGeocode(lat: Double, lon: Double): String?
}
