package com.example.weather_app.data.weather.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather_app.data.database.AppDatabase
import com.example.weather_app.data.weather.local.entity.FavoriteLocationEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavoriteLocationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var favoriteLocationDao: FavoriteLocationDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        favoriteLocationDao = database.favoriteLocationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addFavorite_location_locationAdded() = runTest {
        // Given
        val location = FavoriteLocationEntity(
            cityName = "Cairo",
            countryCode = "EG",
            lat = 30.0,
            lon = 31.0,
            addedAt = System.currentTimeMillis()
        )

        // When
        favoriteLocationDao.addFavorite(location)

        // Then
        val result = favoriteLocationDao.getFavoriteLocations().first()
        assertThat(result.size, `is`(1))
        assertThat(result[0].cityName, `is`("Cairo"))
    }

    @Test
    fun removeFavorite_coordinates_locationRemoved() = runTest {
        // Given
        val location = FavoriteLocationEntity(
            cityName = "Cairo",
            countryCode = "EG",
            lat = 30.0,
            lon = 31.0,
            addedAt = System.currentTimeMillis()
        )
        favoriteLocationDao.addFavorite(location)

        // When
        favoriteLocationDao.removeFavorite(30.0, 31.0)

        // Then
        val result = favoriteLocationDao.getFavoriteLocations().first()
        assertThat(result.isEmpty(), `is`(true))
    }

    @Test
    fun getFavoriteLocations_multipleLocations_orderedByAddedAtDesc() = runTest {
        // Given
        val location1 = FavoriteLocationEntity(
            cityName = "Cairo",
            countryCode = "EG",
            lat = 30.0,
            lon = 31.0,
            addedAt = 1000L
        )
        val location2 = FavoriteLocationEntity(
            cityName = "Alex",
            countryCode = "EG",
            lat = 31.0,
            lon = 29.0,
            addedAt = 2000L
        )
        favoriteLocationDao.addFavorite(location1)
        favoriteLocationDao.addFavorite(location2)

        // When
        val result = favoriteLocationDao.getFavoriteLocations().first()

        // Then
        assertThat(result.size, `is`(2))
        assertThat(result[0].cityName, `is`("Alex"))
        assertThat(result[1].cityName, `is`("Cairo"))
    }
}
