package com.example.weather_app.data.user.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_app.domain.entity.user.AppLanguage
import com.example.weather_app.domain.entity.user.AppTheme
import com.example.weather_app.domain.entity.user.LocationMode
import com.example.weather_app.domain.entity.user.TemperatureUnit
import com.example.weather_app.domain.entity.user.WindSpeedUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserPreferencesDataSourceImplTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreferencesDataSource: UserPreferencesDataSourceImpl

    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testScope = TestScope(UnconfinedTestDispatcher() + Job())

        val testFile = File(
            ApplicationProvider.getApplicationContext<Context>().filesDir,
            "test_datastore_${UUID.randomUUID()}.preferences_pb"
        )

        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope, // Pass the scope here
            produceFile = { testFile }
        )
        userPreferencesDataSource = UserPreferencesDataSourceImpl(dataStore)
    }

    @After
    fun tearDown() {
        // 3. Cancel the scope to release the file lock
        testScope.cancel()
    }

    @Test
    fun userPreferences_default_returnsDefaultValues() = runTest {
        // Given (nothing)

        // When
        val result = userPreferencesDataSource.userPreferences.first()

        // Then
        assertThat(result.temperatureUnit, `is`(TemperatureUnit.CELSIUS))
        assertThat(result.language, `is`(AppLanguage.ENGLISH))
        assertThat(result.locationMode, `is`(LocationMode.GPS))
    }

    @Test
    fun updateTemperatureUnit_unit_updatesCorrectly() = runTest {
        // Given
        val unit = TemperatureUnit.FAHRENHEIT

        // When
        userPreferencesDataSource.updateTemperatureUnit(unit)

        // Then
        val result = userPreferencesDataSource.userPreferences.first()
        assertThat(result.temperatureUnit, `is`(TemperatureUnit.FAHRENHEIT))
    }

    @Test
    fun updateLanguage_language_updatesCorrectly() = runTest {
        // Given
        val lang = AppLanguage.ARABIC

        // When
        userPreferencesDataSource.updateLanguage(lang)

        // Then
        val result = userPreferencesDataSource.userPreferences.first()
        assertThat(result.language, `is`(AppLanguage.ARABIC))
    }

    @Test
    fun updateTheme_theme_updatesCorrectly() = runTest {
        // Given
        val theme = AppTheme.DARK

        // When
        userPreferencesDataSource.updateTheme(theme)

        // Then
        val result = userPreferencesDataSource.userPreferences.first()
        assertThat(result.theme, `is`(AppTheme.DARK))
    }

    @Test
    fun updateSavedLocation_latLon_updatesCorrectly() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0

        // When
        userPreferencesDataSource.updateSavedLocation(lat, lon)

        // Then
        val result = userPreferencesDataSource.userPreferences.first()
        assertThat(result.savedLat, `is`(30.0))
        assertThat(result.savedLon, `is`(31.0))
    }
}