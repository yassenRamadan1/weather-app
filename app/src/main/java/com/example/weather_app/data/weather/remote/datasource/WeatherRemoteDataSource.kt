package com.example.weather_app.data.weather.remote.datasource

import com.example.weather_app.data.weather.remote.dto.CurrentWeatherDto

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherDto>
}