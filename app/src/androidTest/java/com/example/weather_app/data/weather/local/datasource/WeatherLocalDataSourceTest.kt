package com.example.weather_app.data.weather.local.datasource

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather_app.data.database.AppDatabase
import com.example.weather_app.data.weather.local.dao.FavoriteLocationDao
import com.example.weather_app.data.weather.local.dao.ForecastDao
import com.example.weather_app.data.weather.local.dao.WeatherAlertDao
import com.example.weather_app.data.weather.local.dao.WeatherDao
import com.example.weather_app.data.weather.local.entity.WeatherEntity
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
@MediumTest
class WeatherLocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var forecastDao: ForecastDao
    private lateinit var favoriteLocationDao: FavoriteLocationDao
    private lateinit var weatherAlertDao: WeatherAlertDao
    private lateinit var weatherLocalDataSource: WeatherLocalDataSourceImpl

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        weatherDao = database.weatherDao()
        forecastDao = database.forecastDao()
        favoriteLocationDao = database.favoriteLocationDao()
        weatherAlertDao = database.weatherAlertDao()

        weatherLocalDataSource = WeatherLocalDataSourceImpl(
            weatherDao,
            forecastDao,
            favoriteLocationDao,
            weatherAlertDao
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveWeatherData_weatherEntity_dataSaved() = runTest {
        // Given
        val weather = WeatherEntity(
            id = 1, cityName = "Cairo", weatherStateId = 800, countryCode = "EG",
            temperature = 25.0, feelsLike = 26.0, description = "clear sky", iconCode = "01d",
            humidity = 50, windSpeed = 5.0, pressure = 1013, cloudiness = 0,
            visibility = 10000, timestamp = 1000L, lat = 30.0, lon = 31.0
        )

        // When
        weatherLocalDataSource.saveWeatherData(weather)

        // Then
        val result = weatherLocalDataSource.getWeatherData(30.0, 31.0).first()
        assertThat(result?.cityName, `is`("Cairo"))
    }

    @Test
    fun addFavoriteLocation_location_locationAdded() = runTest {
        // Given
        val location = FavoriteLocationEntity(
            cityName = "Cairo",
            countryCode = "EG",
            lat = 30.0,
            lon = 31.0,
            addedAt = System.currentTimeMillis()
        )

        // When
        weatherLocalDataSource.addFavoriteLocation(location)

        // Then
        val result = weatherLocalDataSource.getAllFavoriteLocations().first()
        assertThat(result.size, `is`(1))
        assertThat(result[0].cityName, `is`("Cairo"))
    }

    @Test
    fun deleteWeatherData_coordinates_allWeatherDataDeleted() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val weather = WeatherEntity(
            id = 1, cityName = "Cairo", weatherStateId = 800, countryCode = "EG",
            temperature = 25.0, feelsLike = 26.0, description = "clear sky", iconCode = "01d",
            humidity = 50, windSpeed = 5.0, pressure = 1013, cloudiness = 0,
            visibility = 10000, timestamp = 1000L, lat = lat, lon = lon
        )
        weatherLocalDataSource.saveWeatherData(weather)

        // When
        weatherLocalDataSource.deleteWeatherData(lat, lon)

        // Then
        val result = weatherLocalDataSource.getWeatherData(lat, lon).first()
        assertThat(result == null, `is`(true))
    }
}
