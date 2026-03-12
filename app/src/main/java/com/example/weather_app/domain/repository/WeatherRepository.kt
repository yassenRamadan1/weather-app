package com.example.weather_app.domain.repository

import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.entity.FavoriteLocation
import com.example.weather_app.domain.entity.HourlyWeather
import com.example.weather_app.domain.entity.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double): Flow<Result<Weather>>
    fun getHourlyForecast(lat: Double, lon: Double): Flow<Result<List<HourlyWeather>>>
    fun getDailyForecast(lat: Double, lon: Double): Flow<Result<List<DailyForecast>>>

    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun addFavoriteLocation(location: FavoriteLocation)
    suspend fun deleteFavoriteLocation(lat: Double, lon: Double)
}