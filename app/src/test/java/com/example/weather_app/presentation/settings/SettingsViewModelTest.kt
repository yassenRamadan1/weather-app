package com.example.weather_app.presentation.settings

import com.example.weather_app.MainDispatcherRule
import com.example.weather_app.data.user.repository.FakeLocationProvider
import com.example.weather_app.domain.entity.user.AppLanguage
import com.example.weather_app.domain.entity.user.AppTheme
import com.example.weather_app.domain.entity.user.LocationMode
import com.example.weather_app.domain.usecases.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepo: FakeUserPreferencesRepository
    private lateinit var fakeLocationProvider: FakeLocationProvider
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        fakeRepo = FakeUserPreferencesRepository()
        fakeLocationProvider = FakeLocationProvider()
        
        viewModel = SettingsViewModel(
            ObserveUserPreferencesUseCase(fakeRepo),
            UpdateThemeUseCase(fakeRepo),
            UpdateLanguageUseCase(fakeRepo),
            UpdateTemperatureUnitUseCase(fakeRepo),
            UpdateWindSpeedUnitUseCase(fakeRepo),
            UpdateLocationModeUseCase(fakeRepo),
            UpdateSavedLocationUseCase(fakeRepo),
            IsLocationServicesEnabledUseCase(fakeLocationProvider)
        )
    }

    @Test
    fun setTheme_theme_themeUpdated() = runTest {
        // Given
        val theme = AppTheme.DARK

        // When
        viewModel.setTheme(theme)

        // Then
        assertThat(fakeRepo.userPreferences.first().theme, `is`(AppTheme.DARK))
    }

    @Test
    fun setLanguage_language_languageUpdated() = runTest {
        // Given
        val lang = AppLanguage.ARABIC

        // When
        viewModel.setLanguage(lang)

        // Then
        assertThat(fakeRepo.userPreferences.first().language, `is`(AppLanguage.ARABIC))
    }

    @Test
    fun setLocationMode_mode_modeUpdated() = runTest {
        // Given
        val mode = LocationMode.MAP

        // When
        viewModel.setLocationMode(mode)

        // Then
        assertThat(fakeRepo.userPreferences.first().locationMode, `is`(LocationMode.MAP))
    }
}
