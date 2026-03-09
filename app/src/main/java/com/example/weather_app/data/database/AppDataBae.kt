package com.example.weather_app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_app.data.weather.local.WeatherDao
import com.example.weather_app.data.weather.local.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}