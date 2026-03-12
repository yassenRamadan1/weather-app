package com.example.weather_app.data.weather.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query("SELECT * FROM hourly_weather_cache WHERE lat = :lat AND lon = :lon ORDER BY timestamp ASC")
    fun getHourlyWeather(lat: Double, lon: Double): Flow<List<HourlyWeatherEntity>>

    @Query("DELETE FROM hourly_weather_cache WHERE lat = :lat AND lon = :lon")
    suspend fun deleteHourlyWeather(lat: Double, lon: Double)

    @Insert
    suspend fun insertHourlyWeather(items: List<HourlyWeatherEntity>)

    @Transaction
    suspend fun replaceHourlyWeather(lat: Double, lon: Double, items: List<HourlyWeatherEntity>) {
        deleteHourlyWeather(lat, lon)
        insertHourlyWeather(items)
    }

    @Query("SELECT * FROM daily_forecast_cache WHERE lat = :lat AND lon = :lon ORDER BY timestamp ASC")
    fun getDailyForecast(lat: Double, lon: Double): Flow<List<DailyForecastEntity>>

    @Query("DELETE FROM daily_forecast_cache WHERE lat = :lat AND lon = :lon")
    suspend fun deleteDailyForecast(lat: Double, lon: Double)

    @Insert
    suspend fun insertDailyForecast(items: List<DailyForecastEntity>)

    @Transaction
    suspend fun replaceDailyForecast(lat: Double, lon: Double, items: List<DailyForecastEntity>) {
        deleteDailyForecast(lat, lon)
        insertDailyForecast(items)
    }
}
