package com.example.weather_app.data.weather.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_cache WHERE lat = :lat AND lon = :lon LIMIT 1")
    fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<WeatherEntity?>

    @Query("DELETE FROM weather_cache WHERE lat = :lat AND lon = :lon")
    suspend fun deleteWeatherByCoordinates(lat: Double, lon: Double)
}