package com.example.weather_app.data.weather.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_app.data.weather.local.entity.WeatherAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: WeatherAlertEntity): Long

    @Query("SELECT * FROM weather_alerts ORDER BY startTimeMillis ASC")
    fun getAllAlerts(): Flow<List<WeatherAlertEntity>>

    @Query("SELECT * FROM weather_alerts WHERE isActive = 1")
    suspend fun getActiveAlerts(): List<WeatherAlertEntity>

    @Query("SELECT * FROM weather_alerts WHERE id = :id")
    suspend fun getAlertById(id: Long): WeatherAlertEntity?

    @Query("DELETE FROM weather_alerts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE weather_alerts SET isActive = :isActive WHERE id = :id")
    suspend fun updateActive(id: Long, isActive: Boolean)
}