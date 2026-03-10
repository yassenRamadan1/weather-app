package com.example.weather_app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_app.data.weather.local.ForecastDao
import com.example.weather_app.data.weather.local.WeatherDao
import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import com.example.weather_app.data.weather.local.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class, HourlyWeatherEntity::class, DailyForecastEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao
}
