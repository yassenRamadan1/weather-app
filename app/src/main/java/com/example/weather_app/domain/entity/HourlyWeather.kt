package com.example.weather_app.domain.entity

data class HourlyWeather(
    val timestamp: Long,
    val temperature: Double,
    val iconCode: String,
    val description: String,
    val windSpeed: Double,
    val humidity: Int,
    val pop: Double
)
