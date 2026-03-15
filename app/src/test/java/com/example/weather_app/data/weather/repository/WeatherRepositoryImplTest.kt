package com.example.weather_app.data.weather.repository

import com.example.weather_app.data.weather.local.entity.WeatherEntity
import com.example.weather_app.data.weather.remote.dto.*
import com.example.weather_app.domain.entity.weather.Weather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryImplTest {

    private lateinit var remote: FakeWeatherRemoteDataSource
    private lateinit var local: FakeWeatherLocalDataSource
    private lateinit var userPrefs: FakeUserPreferencesDataSource
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        remote = FakeWeatherRemoteDataSource()
        local = FakeWeatherLocalDataSource()
        userPrefs = FakeUserPreferencesDataSource()
        repository = WeatherRepositoryImpl(remote, local, userPrefs)
    }

    @Test
    fun getCurrentWeather_latLon_returnsCachedThenRemote() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val remoteDto = CurrentWeatherDto(
            name = "Cairo",
            dt = 1000L,
            coord = CoordDto(lat, lon),
            weather = listOf(WeatherDescDto("clear sky", "01d", 800)),
            main = MainDto(25.0, 26.0, 50, 1013),
            wind = WindDto(5.0),
            clouds = CloudsDto(0),
            visibility = 10000,
            sys = SysDto("EG")
        )
        remote.currentWeatherResult = Result.success(remoteDto)

        // When
        val results = repository.getCurrentWeather(lat, lon).toList()

        // Then
        // Should emit at least the remote result (since cache is empty)
        assertThat(results.size, `is`(1))
        assertThat(results[0].getOrNull()?.cityName, `is`("Cairo"))
    }

    @Test
    fun getCurrentWeather_remoteFailure_returnsError() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val exception = Exception("Network error")
        remote.currentWeatherResult = Result.failure(exception)

        // When
        val results = repository.getCurrentWeather(lat, lon).toList()

        // Then
        assertThat(results.size, `is`(1))
        assertThat(results[0].isFailure, `is`(true))
    }
    @Test
    fun getCurrentWeather_cacheHit_returnsCached() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val cachedEntity = WeatherEntity(
            id = 1,
            cityName = "Cairo",
            countryCode = "EG",
            temperature = 25.0,
            feelsLike = 26.0,
            description = "clear sky",
            iconCode = "01d",
            humidity = 50,
            windSpeed = 5.0,
            pressure = 1013,
            cloudiness = 0,
            visibility = 10000,
            timestamp = System.currentTimeMillis(),
            lat = lat,
            lon = lon,
            weatherStateId = 800
        )
        local.saveWeatherData(cachedEntity)
        // When
        val results = repository.getCurrentWeather(lat, lon).toList()

        // Then
        assertThat(results[0].getOrNull()?.cityName, `is`("Cairo"))
    }
}
