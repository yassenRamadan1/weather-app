package com.example.weather_app.domain.entity

data class DailyForecast(
    val timestamp: Long,
    val minTemp: Double,
    val maxTemp: Double,
    val iconCode: String,
    val description: String,
    val humidity: Int,
    val windSpeed: Double
)
