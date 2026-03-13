package com.example.weather_app.data.weather.remote.datasource

import com.example.weather_app.data.weather.remote.dto.CurrentWeatherDto
import com.example.weather_app.data.weather.remote.dto.ForecastResponseDto

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherDto>

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<ForecastResponseDto>
}