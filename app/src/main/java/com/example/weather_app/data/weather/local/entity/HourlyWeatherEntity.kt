package com.example.weather_app.data.weather.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hourly_weather_cache",
    indices = [Index(value = ["lat", "lon"])]
)
data class HourlyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val temperature: Double,
    val weatherStateId: Int,
    val iconCode: String,
    val description: String,
    val windSpeed: Double,
    val humidity: Int,
    val pop: Double
)
