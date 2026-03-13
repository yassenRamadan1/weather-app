package com.example.weather_app.data.weather.local.datasource

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.FavoriteLocationEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import com.example.weather_app.data.weather.local.entity.WeatherAlertEntity
import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun saveWeatherData(weatherData: WeatherEntity)
    suspend fun getWeatherData(lat: Double, lon: Double): Flow<WeatherEntity?>

    suspend fun replaceHourlyForecast(lat: Double, lon: Double, items: List<HourlyWeatherEntity>)
    fun getHourlyForecast(lat: Double, lon: Double): Flow<List<HourlyWeatherEntity>>

    suspend fun replaceDailyForecast(lat: Double, lon: Double, items: List<DailyForecastEntity>)
    fun getDailyForecast(lat: Double, lon: Double): Flow<List<DailyForecastEntity>>

    fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>>
    suspend fun deleteFavoriteLocation(lat: Double, lon: Double)
    suspend fun addFavoriteLocation(favoriteLocationEntity: FavoriteLocationEntity)
    suspend fun deleteWeatherData(lat: Double, lon: Double)
    fun getAllAlerts(): Flow<List<WeatherAlertEntity>>

    suspend fun insertAlert(entity: WeatherAlertEntity): Long

    suspend fun deleteAlert(id: Long)

    suspend fun updateActive(id: Long, isActive: Boolean)

    suspend fun getActiveAlerts(): List<WeatherAlertEntity>
}