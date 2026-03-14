package com.example.weather_app.presentation.home

import com.example.weather_app.MainDispatcherRule
import com.example.weather_app.domain.entity.user.LocationResult
import com.example.weather_app.domain.usecases.*
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeWeatherRepo: FakeWeatherRepository
    private lateinit var fakeUserRepo: FakeUserPreferencesRepository
    private lateinit var uiMapper: HomeUiMapper
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        fakeWeatherRepo = FakeWeatherRepository()
        fakeUserRepo = FakeUserPreferencesRepository()
        uiMapper = mockk() // We can use mockk for mapper if needed, or real one
        
        val getPreferredLocationUseCase = GetPreferredLocationUseCase(fakeUserRepo, fakeUserRepo)
        val getWeatherUseCase = GetWeatherUseCase(fakeWeatherRepo)
        val getHourlyForecastUseCase = GetHourlyForecastUseCase(fakeWeatherRepo)
        val getDailyForecastUseCase = GetDailyForecastUseCase(fakeWeatherRepo)
        val observeUserPreferencesUseCase = ObserveUserPreferencesUseCase(fakeUserRepo)

        viewModel = HomeViewModel(
            getPreferredLocationUseCase,
            getWeatherUseCase,
            getHourlyForecastUseCase,
            getDailyForecastUseCase,
            observeUserPreferencesUseCase,
            uiMapper
        )
    }

    @Test
    fun resolveLocationAndLoadWeather_needPermission_uiStateNeedPermission() = runTest {
        // Given
        fakeUserRepo.locationResult = LocationResult.NeedPermission

        // When
        viewModel.resolveLocationAndLoadWeather()

        // Then
        assertThat(viewModel.uiState.value, `is`(HomeUiState.NeedLocationPermission))
    }

    @Test
    fun onPermissionRequestLaunched_noInput_hasLaunchedUpdated() = runTest {
        // When
        viewModel.onPermissionRequestLaunched()

        // Then
        assertThat(viewModel.hasLaunchedPermissionRequest.value, `is`(true))
    }

    @Test
    fun resolveLocationAndLoadWeather_gpsDisabled_uiStateGpsDisabled() = runTest {
        // Given
        fakeUserRepo.locationResult = LocationResult.GpsDisabled

        // When
        viewModel.resolveLocationAndLoadWeather()

        // Then
        assertThat(viewModel.uiState.value, `is`(HomeUiState.GpsDisabled))
    }
}
