package com.example.weather_app.data.weather.local.datasource

import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun saveWeatherData(weatherData: WeatherEntity)
    suspend fun getWeatherData(lat: Double,lon: Double): Flow<WeatherEntity?>
}