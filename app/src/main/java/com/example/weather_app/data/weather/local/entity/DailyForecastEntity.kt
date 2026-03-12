package com.example.weather_app.data.weather.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_forecast_cache",
    indices = [Index(value = ["lat", "lon"])]
)
data class DailyForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val minTemp: Double,
    val maxTemp: Double,
    val iconCode: String,
    val description: String,
    val humidity: Int,
    val windSpeed: Double
)
