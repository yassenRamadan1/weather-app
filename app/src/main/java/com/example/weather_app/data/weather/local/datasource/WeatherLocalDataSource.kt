package com.example.weather_app.data.weather.local.datasource

import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun saveWeatherData(weatherData: WeatherEntity)
    suspend fun getWeatherData(lat: Double, lon: Double): Flow<WeatherEntity?>

    suspend fun replaceHourlyForecast(lat: Double, lon: Double, items: List<HourlyWeatherEntity>)
    fun getHourlyForecast(lat: Double, lon: Double): Flow<List<HourlyWeatherEntity>>

    suspend fun replaceDailyForecast(lat: Double, lon: Double, items: List<DailyForecastEntity>)
    fun getDailyForecast(lat: Double, lon: Double): Flow<List<DailyForecastEntity>>
}