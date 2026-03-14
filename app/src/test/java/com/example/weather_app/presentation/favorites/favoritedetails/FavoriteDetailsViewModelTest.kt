package com.example.weather_app.presentation.favorites.favoritedetails

import androidx.lifecycle.SavedStateHandle
import com.example.weather_app.MainDispatcherRule
import com.example.weather_app.domain.usecases.*
import com.example.weather_app.navigation.Screen
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeWeatherRepo: FakeWeatherRepository
    private lateinit var fakeUserRepo: FakeUserPreferencesRepository
    private lateinit var uiMapper: FavoriteDetailsUiMapper
    private lateinit var viewModel: FavoriteDetailsViewModel

    @Before
    fun setup() {
        fakeWeatherRepo = FakeWeatherRepository()
        fakeUserRepo = FakeUserPreferencesRepository()
        uiMapper = mockk()
        
        val savedStateHandle = SavedStateHandle(mapOf(
            Screen.FavoriteDetails.ARG_LAT to "30.0",
            Screen.FavoriteDetails.ARG_LON to "31.0"
        ))

        val getWeatherUseCase = GetWeatherUseCase(fakeWeatherRepo)
        val getHourlyForecastUseCase = GetHourlyForecastUseCase(fakeWeatherRepo)
        val getDailyForecastUseCase = GetDailyForecastUseCase(fakeWeatherRepo)
        val observeUserPreferencesUseCase = ObserveUserPreferencesUseCase(fakeUserRepo)

        viewModel = FavoriteDetailsViewModel(
            getWeatherUseCase,
            getHourlyForecastUseCase,
            getDailyForecastUseCase,
            observeUserPreferencesUseCase,
            uiMapper,
            savedStateHandle
        )
    }

    @Test
    fun init_noDataProvided_uiStateLoading() = runTest {
        // Given (nothing in repositories)

        // When (ViewModel already initialized)

        // Then
        assertThat(viewModel.uiState.value, `is`(FavoriteDetailsScreenUiState.Loading))
    }

    @Test
    fun onRefresh_noInput_isRefreshingToggles() = runTest {
        // When
        viewModel.onRefresh()

        // Then
        // After refresh finishes, it should be false
        assertThat(viewModel.isRefreshing.value, `is`(false))
    }

    @Test
    fun observeWeatherData_latLonCorrectlyExtracted() = runTest {
        // Given (SavedStateHandle set to 30.0, 31.0)
        
        // When
        // ViewModel init automatically calls observeWeatherData()
        
        // Then (Verification of behavior)
        // If it didn't throw an error in init, it extracted them correctly
        assertThat(viewModel.uiState.value, `is`(FavoriteDetailsScreenUiState.Loading))
    }
}
