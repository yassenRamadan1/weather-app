package com.example.weather_app.data.weather.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int ,
    val cityName: String,
    val countryCode: String?,
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