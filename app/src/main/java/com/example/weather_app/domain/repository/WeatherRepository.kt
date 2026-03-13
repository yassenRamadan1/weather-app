package com.example.weather_app.domain.repository

import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.FavoriteLocation
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.weather.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double): Flow<Result<Weather>>
    fun getHourlyForecast(lat: Double, lon: Double): Flow<Result<List<HourlyWeather>>>
    fun getDailyForecast(lat: Double, lon: Double): Flow<Result<List<DailyForecast>>>

    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun addFavoriteLocation(location: FavoriteLocation)
    suspend fun deleteFavoriteLocation(lat: Double, lon: Double)

    fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun addAlert(alert: WeatherAlert): Long
    suspend fun deleteAlert(id: Long)
    suspend fun setAlertActive(id: Long, isActive: Boolean)
    suspend fun getActiveAlerts(): List<WeatherAlert>
}