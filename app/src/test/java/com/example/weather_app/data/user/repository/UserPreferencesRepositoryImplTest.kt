package com.example.weather_app.data.user.repository

import android.location.Location
import com.example.weather_app.data.weather.repository.FakeUserPreferencesDataSource
import com.example.weather_app.domain.entity.user.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserPreferencesRepositoryImplTest {

    private lateinit var locationProvider: FakeLocationProvider
    private lateinit var userPreferencesDataSource: FakeUserPreferencesDataSource
    private lateinit var geocodingProvider: FakeGeocodingProvider
    private lateinit var repository: UserPreferencesRepositoryImpl

    @Before
    fun setup() {
        locationProvider = FakeLocationProvider()
        userPreferencesDataSource = FakeUserPreferencesDataSource()
        geocodingProvider = FakeGeocodingProvider()
        repository = UserPreferencesRepositoryImpl(
            locationProvider,
            userPreferencesDataSource,
            geocodingProvider
        )
    }

    @Test
    fun getPreferredLocation_mapMode_returnsSavedLocation() = runTest {
        // Given
        val prefs = UserPreferences(
            locationMode = LocationMode.MAP,
            savedLat = 30.0,
            savedLon = 31.0
        )
        geocodingProvider.cityName = "Cairo"

        // When
        val result = repository.getPreferredLocation(prefs)

        // Then
        assertThat(result is LocationResult.Success, `is`(true))
        val success = result as LocationResult.Success
        assertThat(success.lat, `is`(30.0))
        assertThat(success.cityName, `is`("Cairo"))
        assertThat(success.source, `is`(LocationSource.SAVED_PREFERENCES))
    }

    @Test
    fun getPreferredLocation_gpsMode_returnsGpsLocation() = runTest {
        // Given
        val prefs = UserPreferences(locationMode = LocationMode.GPS)
        val mockLocation = mockk<Location> {
            every { latitude } returns 30.0
            every { longitude } returns 31.0
        }
        locationProvider.currentLocation = mockLocation
        geocodingProvider.cityName = "Cairo"

        // When
        val result = repository.getPreferredLocation(prefs)

        // Then
        assertThat(result is LocationResult.Success, `is`(true))
        val success = result as LocationResult.Success
        assertThat(success.lat, `is`(30.0))
        assertThat(success.source, `is`(LocationSource.GPS))
    }

    @Test
    fun getPreferredLocation_gpsNoPermission_returnsNeedPermission() = runTest {
        // Given
        val prefs = UserPreferences(locationMode = LocationMode.GPS)
        locationProvider.hasPermission = false

        // When
        val result = repository.getPreferredLocation(prefs)

        // Then
        assertThat(result is LocationResult.NeedPermission, `is`(true))
    }
}
