package com.example.weather_app.domain.entity.weather

data class Weather(
    val cityName: String,
    val weatherStateId: Int,
    val countryCode: String,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val cloudiness: Int,
    val visibility: Int,
    val timestamp: Long,
    val lat: Double,
    val lon: Double
)