package com.example.weather_app.data.weather.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather_app.data.database.AppDatabase
import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
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
class ForecastDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var forecastDao: ForecastDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        forecastDao = database.forecastDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun replaceHourlyWeather_list_newWeatherCached() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val hourlyList = listOf(
            HourlyWeatherEntity(lat = lat, lon = lon, timestamp = 1000L, temperature = 25.0, iconCode = "01d", description = "clear sky", windSpeed = 5.0, humidity = 50, pop = 0.1),
            HourlyWeatherEntity(lat = lat, lon = lon, timestamp = 2000L, temperature = 24.0, iconCode = "01d", description = "clear sky", windSpeed = 4.0, humidity = 55, pop = 0.2)
        )

        // When
        forecastDao.replaceHourlyWeather(lat, lon, hourlyList)

        // Then
        val result = forecastDao.getHourlyWeather(lat, lon).first()
        assertThat(result.size, `is`(2))
        assertThat(result[0].temperature, `is`(25.0))
        assertThat(result[1].temperature, `is`(24.0))
    }

    @Test
    fun replaceDailyForecast_list_newForecastCached() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val dailyList = listOf(
            DailyForecastEntity(lat = lat, lon = lon, timestamp = 1000L, minTemp = 20.0, maxTemp = 30.0, iconCode = "01d", description = "clear sky", humidity = 50, windSpeed = 5.0),
            DailyForecastEntity(lat = lat, lon = lon, timestamp = 2000L, minTemp = 21.0, maxTemp = 31.0, iconCode = "01d", description = "clear sky", humidity = 55, windSpeed = 4.0)
        )

        // When
        forecastDao.replaceDailyForecast(lat, lon, dailyList)

        // Then
        val result = forecastDao.getDailyForecast(lat, lon).first()
        assertThat(result.size, `is`(2))
        assertThat(result[0].minTemp, `is`(20.0))
        assertThat(result[1].minTemp, `is`(21.0))
    }

    @Test
    fun deleteHourlyWeather_coordinates_cacheCleared() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val hourlyList = listOf(
            HourlyWeatherEntity(lat = lat, lon = lon, timestamp = 1000L, temperature = 25.0, iconCode = "01d", description = "clear sky", windSpeed = 5.0, humidity = 50, pop = 0.1)
        )
        forecastDao.insertHourlyWeather(hourlyList)

        // When
        forecastDao.deleteHourlyWeather(lat, lon)

        // Then
        val result = forecastDao.getHourlyWeather(lat, lon).first()
        assertThat(result.isEmpty(), `is`(true))
    }
}
