package com.example.weather_app.data.user.repository

import com.example.weather_app.domain.repository.GeocodingProvider

class FakeGeocodingProvider : GeocodingProvider {
    var cityName: String? = null
    override suspend fun reverseGeocode(lat: Double, lon: Double): String? = cityName
}
