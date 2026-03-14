package com.example.weather_app.data.weather.repository

import com.example.weather_app.data.weather.remote.datasource.WeatherRemoteDataSource
import com.example.weather_app.data.weather.remote.dto.*

class FakeWeatherRemoteDataSource : WeatherRemoteDataSource {
    var currentWeatherResult: Result<CurrentWeatherDto>? = null
    var forecastResult: Result<ForecastResponseDto>? = null

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherDto> {
        return currentWeatherResult ?: Result.failure(Exception("No result set"))
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<ForecastResponseDto> {
        return forecastResult ?: Result.failure(Exception("No result set"))
    }
}
