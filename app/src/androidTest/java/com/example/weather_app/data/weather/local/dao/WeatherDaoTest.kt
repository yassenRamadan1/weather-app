package com.example.weather_app.data.weather.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather_app.data.database.AppDatabase
import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertWeather_weatherEntity_weatherCached() = runTest {
        // Given
        val weather = WeatherEntity(
            id = 1, cityName = "Cairo", weatherStateId = 800, countryCode = "EG",
            temperature = 25.0, feelsLike = 26.0, description = "clear sky", iconCode = "01d",
            humidity = 50, windSpeed = 5.0, pressure = 1013, cloudiness = 0,
            visibility = 10000, timestamp = 1000L, lat = 30.0, lon = 31.0
        )

        // When
        weatherDao.insertWeather(weather)

        // Then
        val result = weatherDao.getWeatherByCoordinates(30.0, 31.0).first()
        assertThat(result?.cityName, `is`("Cairo"))
    }

    @Test
    fun getWeatherByCoordinates_approximateCoordinates_weatherReturned() = runTest {
        // Given
        val weather = WeatherEntity(
            id = 1, cityName = "Cairo", weatherStateId = 800, countryCode = "EG",
            temperature = 25.0, feelsLike = 26.0, description = "clear sky", iconCode = "01d",
            humidity = 50, windSpeed = 5.0, pressure = 1013, cloudiness = 0,
            visibility = 10000, timestamp = 1000L, lat = 30.0, lon = 31.0
        )
        weatherDao.insertWeather(weather)

        // When
        val result = weatherDao.getWeatherByCoordinates(30.05, 31.05).first()

        // Then
        assertThat(result?.cityName, `is`("Cairo"))
    }

    @Test
    fun deleteWeatherByCoordinates_coordinates_cacheCleared() = runTest {
        // Given
        val weather = WeatherEntity(
            id = 1, cityName = "Cairo", weatherStateId = 800, countryCode = "EG",
            temperature = 25.0, feelsLike = 26.0, description = "clear sky", iconCode = "01d",
            humidity = 50, windSpeed = 5.0, pressure = 1013, cloudiness = 0,
            visibility = 10000, timestamp = 1000L, lat = 30.0, lon = 31.0
        )
        weatherDao.insertWeather(weather)

        // When
        weatherDao.deleteWeatherByCoordinates(30.0, 31.0)

        // Then
        val result = weatherDao.getWeatherByCoordinates(30.0, 31.0).first()
        assertThat(result, nullValue())
    }
}
