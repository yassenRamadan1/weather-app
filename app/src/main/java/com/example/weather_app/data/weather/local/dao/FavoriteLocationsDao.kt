package com.example.weather_app.data.weather.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.example.weather_app.data.weather.local.entity.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {

    @Query("SELECT * FROM favorite_locations ORDER BY addedAt DESC")
    fun getFavoriteLocations(): Flow<List<FavoriteLocationEntity>>

    @Insert(onConflict = IGNORE)
    suspend fun addFavorite(location: FavoriteLocationEntity)

    @Query("DELETE FROM favorite_locations WHERE lat = :lat AND lon = :lon")
    suspend fun removeFavorite(lat: Double, lon: Double)

}