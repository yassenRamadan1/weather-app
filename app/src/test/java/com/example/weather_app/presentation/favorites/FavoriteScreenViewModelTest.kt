package com.example.weather_app.presentation.favorites

import com.example.weather_app.MainDispatcherRule
import com.example.weather_app.domain.entity.weather.FavoriteLocation
import com.example.weather_app.domain.usecases.AddFavoriteLocationUseCase
import com.example.weather_app.domain.usecases.DeleteFavoriteLocationUseCase
import com.example.weather_app.domain.usecases.FakeWeatherRepository
import com.example.weather_app.domain.usecases.GetFavoriteLocationsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var viewModel: FavoriteScreenViewModel

    @Before
    fun setup() {
        fakeRepository = FakeWeatherRepository()
        val getFavoriteLocations = GetFavoriteLocationsUseCase(fakeRepository)
        val deleteFavoriteLocation = DeleteFavoriteLocationUseCase(fakeRepository)
        val addFavoriteLocationUseCase = AddFavoriteLocationUseCase(fakeRepository)
        viewModel = FavoriteScreenViewModel(
            getFavoriteLocations,
            deleteFavoriteLocation,
            addFavoriteLocationUseCase
        )
    }

    @Test
    fun loadFavoriteLocations_emptyList_uiStateEmpty() = runTest {
        // Given (nothing in fake repository)

        // When
        viewModel.loadFavoriteLocations()

        // Then
        assertThat(viewModel.uiState.value, `is`(FavoritesScreenUiState.Empty))
    }

    @Test
    fun loadFavoriteLocations_hasLocations_uiStateSuccess() = runTest {
        // Given
        val location = FavoriteLocation("Cairo", "EG", 30.0, 31.0)
        fakeRepository.addFavoriteLocation(location)

        // When
        viewModel.loadFavoriteLocations()

        // Then
        val state = viewModel.uiState.value
        assertThat(state is FavoritesScreenUiState.Success, `is`(true))
        assertThat((state as FavoritesScreenUiState.Success).favoriteLocations.size, `is`(1))
    }

    @Test
    fun addFavoriteLocation_validInput_locationAdded() = runTest {
        // Given
        val lat = 30.0
        val lon = 31.0
        val name = "Cairo"
        val country = "EG"

        // When
        viewModel.addFavoriteLocation(lat, lon, name, country)

        // Then
        assertThat(fakeRepository.favoritesList.size, `is`(1))
        assertThat(fakeRepository.favoritesList[0].cityName, `is`("Cairo"))
    }

    @Test
    fun removeFavoriteLocation_coordinates_locationRemoved() = runTest {
        // Given
        val location = FavoriteLocation("Cairo", "EG", 30.0, 31.0)
        fakeRepository.addFavoriteLocation(location)

        // When
        viewModel.removeFavoriteLocation(30.0, 31.0)

        // Then
        assertThat(fakeRepository.favoritesList.isEmpty(), `is`(true))
    }
}
