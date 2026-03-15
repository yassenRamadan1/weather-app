package com.example.weather_app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_app.data.weather.local.dao.FavoriteLocationDao
import com.example.weather_app.data.weather.local.dao.ForecastDao
import com.example.weather_app.data.weather.local.dao.WeatherAlertDao
import com.example.weather_app.data.weather.local.dao.WeatherDao
import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.FavoriteLocationEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import com.example.weather_app.data.weather.local.entity.WeatherAlertEntity
import com.example.weather_app.data.weather.local.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class, HourlyWeatherEntity::class, DailyForecastEntity::class, FavoriteLocationEntity::class, WeatherAlertEntity::class],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao
    abstract fun favoriteLocationDao(): FavoriteLocationDao
    abstract fun weatherAlertDao(): WeatherAlertDao
}
