package com.example.weather_app.domain.repository

import com.example.weather_app.domain.entity.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double): Flow<Result<Weather>>
}