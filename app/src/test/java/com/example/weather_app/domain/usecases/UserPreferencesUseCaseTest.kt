package com.example.weather_app.domain.usecases

import com.example.weather_app.data.user.repository.FakeLocationProvider
import com.example.weather_app.domain.entity.user.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserPreferencesUseCaseTest {

    private lateinit var fakeRepository: FakeUserPreferencesRepository
    private lateinit var fakeLocationProvider: FakeLocationProvider
    private lateinit var updateThemeUseCase: UpdateThemeUseCase
    private lateinit var updateLanguageUseCase: UpdateLanguageUseCase
    private lateinit var updateLocationModeUseCase: UpdateLocationModeUseCase
    private lateinit var updateTemperatureUnitUseCase: UpdateTemperatureUnitUseCase
    private lateinit var updateWindSpeedUnitUseCase: UpdateWindSpeedUnitUseCase
    private lateinit var updateSavedLocationUseCase: UpdateSavedLocationUseCase
    private lateinit var observeUserPreferencesUseCase: ObserveUserPreferencesUseCase
    private lateinit var getPreferredLocationUseCase: GetPreferredLocationUseCase
    private lateinit var isLocationServicesEnabledUseCase: IsLocationServicesEnabledUseCase

    @Before
    fun setup() {
        fakeRepository = FakeUserPreferencesRepository()
        fakeLocationProvider = FakeLocationProvider()
        updateThemeUseCase = UpdateThemeUseCase(fakeRepository)
        updateLanguageUseCase = UpdateLanguageUseCase(fakeRepository)
        updateLocationModeUseCase = UpdateLocationModeUseCase(fakeRepository)
        updateTemperatureUnitUseCase = UpdateTemperatureUnitUseCase(fakeRepository)
        updateWindSpeedUnitUseCase = UpdateWindSpeedUnitUseCase(fakeRepository)
        updateSavedLocationUseCase = UpdateSavedLocationUseCase(fakeRepository)
        observeUserPreferencesUseCase = ObserveUserPreferencesUseCase(fakeRepository)
        getPreferredLocationUseCase = GetPreferredLocationUseCase(fakeRepository, fakeRepository)
        isLocationServicesEnabledUseCase = IsLocationServicesEnabledUseCase(fakeLocationProvider)
    }

    @Test
    fun updateThemeUseCase_theme_themeUpdated() = runTest {
        // Given
        val theme = AppTheme.DARK

        // When
        updateThemeUseCase(theme)

        // Then
        assertThat(fakeRepository.userPreferences.first().theme, `is`(AppTheme.DARK))
    }

    @Test
    fun updateLanguageUseCase_language_languageUpdated() = runTest {
        // Given
        val lang = AppLanguage.ARABIC

        // When
        updateLanguageUseCase(lang)

        // Then
        assertThat(fakeRepository.userPreferences.first().language, `is`(AppLanguage.ARABIC))
    }

    @Test
    fun updateLocationModeUseCase_mode_modeUpdated() = runTest {
        // Given
        val mode = LocationMode.MAP

        // When
        updateLocationModeUseCase(mode)

        // Then
        assertThat(fakeRepository.userPreferences.first().locationMode, `is`(LocationMode.MAP))
    }

    @Test
    fun updateTemperatureUnitUseCase_unit_unitUpdated() = runTest {
        // Given
        val unit = TemperatureUnit.FAHRENHEIT

        // When
        updateTemperatureUnitUseCase(unit)

        // Then
        assertThat(fakeRepository.userPreferences.first().temperatureUnit, `is`(TemperatureUnit.FAHRENHEIT))
    }

    @Test
    fun updateWindSpeedUnitUseCase_unit_unitUpdated() = runTest {
        // Given
        val unit = WindSpeedUnit.MILES_PER_HOUR

        // When
        updateWindSpeedUnitUseCase(unit)

        // Then
        assertThat(fakeRepository.userPreferences.first().windSpeedUnit, `is`(WindSpeedUnit.MILES_PER_HOUR))
    }

    @Test
    fun updateSavedLocationUseCase_latLon_savedLocationUpdated() = runTest {
        // When
        updateSavedLocationUseCase(30.0, 31.0)

        // Then
        val prefs = fakeRepository.userPreferences.first()
        assertThat(prefs.savedLat, `is`(30.0))
        assertThat(prefs.savedLon, `is`(31.0))
    }

    @Test
    fun observeUserPreferencesUseCase_noInput_returnsPreferencesFlow() = runTest {
        // When
        val result = observeUserPreferencesUseCase().first()

        // Then
        assertThat(result.temperatureUnit, `is`(TemperatureUnit.CELSIUS))
    }

    @Test
    fun getPreferredLocationUseCase_noInput_returnsLocationResult() = runTest {
        // Given
        val expectedResult = LocationResult.Success(30.0, 31.0, "Cairo", LocationSource.GPS, false)
        fakeRepository.locationResult = expectedResult

        // When
        val result = getPreferredLocationUseCase()

        // Then
        assertThat(result, `is`(expectedResult))
    }

    @Test
    fun isLocationServicesEnabledUseCase_noInput_returnsEnabledStatus() {
        // Given
        fakeLocationProvider.isEnabled = true

        // When
        val result = isLocationServicesEnabledUseCase()

        // Then
        assertThat(result, `is`(true))
    }
}
