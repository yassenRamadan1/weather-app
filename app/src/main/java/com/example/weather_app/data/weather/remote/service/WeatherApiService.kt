package com.example.weather_app.data.weather.remote.service

import com.example.weather_app.data.weather.remote.dto.CurrentWeatherDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
    ): CurrentWeatherDto
}