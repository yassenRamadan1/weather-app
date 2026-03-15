package com.example.weather_app.data.weather.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val alertType: String,
    val conditionMode: String,
    val temperatureThreshold: Double?,
    val windThreshold: Double?,
    val cloudinessThreshold: Int?,
    val isActive: Boolean,
    val isRepeated: Boolean,
    val lat: Double,
    val lon: Double,
    val cityName: String?
)