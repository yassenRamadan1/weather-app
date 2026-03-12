package com.example.weather_app.data.weather.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_locations",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
data class FavoriteLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val countryCode: String,
    val lat: Double,
    val lon: Double,
    val addedAt: Long = System.currentTimeMillis()
)