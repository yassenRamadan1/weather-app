package com.example.weather_app.domain.entity

data class FavoriteLocation(
    val cityName: String,
    val countryCode: String,
    val lat: Double,
    val lon: Double
)