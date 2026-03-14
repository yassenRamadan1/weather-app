package com.example.weather_app.data.weather.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather_app.data.database.AppDatabase
import com.example.weather_app.data.weather.local.entity.WeatherAlertEntity
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
class WeatherAlertDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var weatherAlertDao: WeatherAlertDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        weatherAlertDao = database.weatherAlertDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_alert_alertInserted() = runTest {
        // Given
        val alert = WeatherAlertEntity(
            startTimeMillis = 1000L,
            endTimeMillis = 2000L,
            alertType = "ALARM",
            conditionMode = "WEATHER",
            temperatureThreshold = 30.0,
            windThreshold = null,
            cloudinessThreshold = null,
            isActive = true,
            lat = 30.0,
            lon = 31.0,
            cityName = "Cairo"
        )

        // When
        val id = weatherAlertDao.insert(alert)

        // Then
        val result = weatherAlertDao.getAlertById(id)
        assertThat(result?.cityName, `is`("Cairo"))
    }

    @Test
    fun getActiveAlerts_alertList_onlyActiveReturned() = runTest {
        // Given
        val alert1 = WeatherAlertEntity(id = 1, startTimeMillis = 1000L, endTimeMillis = 2000L, alertType = "ALARM", conditionMode = "WEATHER", temperatureThreshold = 30.0, windThreshold = null, cloudinessThreshold = null, isActive = true, lat = 30.0, lon = 31.0, cityName = "Cairo")
        val alert2 = WeatherAlertEntity(id = 2, startTimeMillis = 3000L, endTimeMillis = 4000L, alertType = "ALARM", conditionMode = "WEATHER", temperatureThreshold = 35.0, windThreshold = null, cloudinessThreshold = null, isActive = false, lat = 30.0, lon = 31.0, cityName = "Alex")
        weatherAlertDao.insert(alert1)
        weatherAlertDao.insert(alert2)

        // When
        val result = weatherAlertDao.getActiveAlerts()

        // Then
        assertThat(result.size, `is`(1))
        assertThat(result[0].cityName, `is`("Cairo"))
    }

    @Test
    fun deleteById_id_alertRemoved() = runTest {
        // Given
        val alert = WeatherAlertEntity(id = 1, startTimeMillis = 1000L, endTimeMillis = 2000L, alertType = "ALARM", conditionMode = "WEATHER", temperatureThreshold = 30.0, windThreshold = null, cloudinessThreshold = null, isActive = true, lat = 30.0, lon = 31.0, cityName = "Cairo")
        val id = weatherAlertDao.insert(alert)

        // When
        weatherAlertDao.deleteById(id)

        // Then
        val result = weatherAlertDao.getAlertById(id)
        assertThat(result == null, `is`(true))
    }

    @Test
    fun updateActive_idAndStatus_statusUpdated() = runTest {
        // Given
        val alert = WeatherAlertEntity(id = 1, startTimeMillis = 1000L, endTimeMillis = 2000L, alertType = "ALARM", conditionMode = "WEATHER", temperatureThreshold = 30.0, windThreshold = null, cloudinessThreshold = null, isActive = true, lat = 30.0, lon = 31.0, cityName = "Cairo")
        val id = weatherAlertDao.insert(alert)

        // When
        weatherAlertDao.updateActive(id, false)

        // Then
        val result = weatherAlertDao.getAlertById(id)
        assertThat(result?.isActive, `is`(false))
    }
}
