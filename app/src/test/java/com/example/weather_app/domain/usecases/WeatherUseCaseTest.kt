package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.FavoriteLocation
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.weather.Weather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherUseCaseTest {

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var addFavoriteLocationUseCase: AddFavoriteLocationUseCase
    private lateinit var deleteFavoriteLocationUseCase: DeleteFavoriteLocationUseCase
    private lateinit var getDailyForecastUseCase: GetDailyForecastUseCase
    private lateinit var getHourlyForecastUseCase: GetHourlyForecastUseCase
    private lateinit var getFavoriteLocationsUseCase: GetFavoriteLocationsUseCase

    @Before
    fun setup() {
        fakeRepository = FakeWeatherRepository()
        getWeatherUseCase = GetWeatherUseCase(fakeRepository)
        addFavoriteLocationUseCase = AddFavoriteLocationUseCase(fakeRepository)
        deleteFavoriteLocationUseCase = DeleteFavoriteLocationUseCase(fakeRepository)
        getDailyForecastUseCase = GetDailyForecastUseCase(fakeRepository)
        getHourlyForecastUseCase = GetHourlyForecastUseCase(fakeRepository)
        getFavoriteLocationsUseCase = GetFavoriteLocationsUseCase(fakeRepository)
    }

    @Test
    fun getWeatherUseCase_latLon_returnsWeather() = runTest {
        // Given
        val weather = Weather("Cairo", 800, "EG", 25.0, 26.0, "clear sky", "01d", 50, 5.0, 1013, 0, 10000, 1000L, 30.0, 31.0)
        fakeRepository.currentWeather = Result.success(weather)

        // When
        val result = getWeatherUseCase(30.0, 31.0).first()

        // Then
        assertThat(result.getOrNull()?.cityName, `is`("Cairo"))
    }

    @Test
    fun getDailyForecastUseCase_latLon_returnsDailyList() = runTest {
        // Given
        val dailyList = listOf(
            DailyForecast(1000L, 20.0, 30.0, 800, "01d", "clear", 50, 5.0)
        )
        fakeRepository.dailyForecast = Result.success(dailyList)

        // When
        val result = getDailyForecastUseCase(30.0, 31.0).first()

        // Then
        assertThat(result.getOrNull()?.size, `is`(1))
        assertThat(result.getOrNull()?.get(0)?.description, `is`("clear"))
    }

    @Test
    fun getHourlyForecastUseCase_latLon_returnsHourlyList() = runTest {
        // Given
        val hourlyList = listOf(
            HourlyWeather(1000L, 25.0, 800, "01d", "clear", 5.0, 50, 0.1)
        )
        fakeRepository.hourlyForecast = Result.success(hourlyList)

        // When
        val result = getHourlyForecastUseCase(30.0, 31.0).first()

        // Then
        assertThat(result.getOrNull()?.size, `is`(1))
        assertThat(result.getOrNull()?.get(0)?.description, `is`("clear"))
    }

    @Test
    fun addFavoriteLocationUseCase_location_locationAdded() = runTest {
        // Given
        val location = FavoriteLocation("Cairo", "EG", 30.0, 31.0)

        // When
        addFavoriteLocationUseCase(location)

        // Then
        assertThat(fakeRepository.favoritesList.size, `is`(1))
        assertThat(fakeRepository.favoritesList[0].cityName, `is`("Cairo"))
    }

    @Test
    fun getFavoriteLocationsUseCase_noInput_returnsFavoriteList() = runTest {
        // Given
        fakeRepository.addFavoriteLocation(FavoriteLocation("Cairo", "EG", 30.0, 31.0))

        // When
        val result = getFavoriteLocationsUseCase().first()

        // Then
        assertThat(result.size, `is`(1))
        assertThat(result[0].cityName, `is`("Cairo"))
    }

    @Test
    fun deleteFavoriteLocationUseCase_coordinates_locationDeleted() = runTest {
        // Given
        val location = FavoriteLocation("Cairo", "EG", 30.0, 31.0)
        fakeRepository.addFavoriteLocation(location)

        // When
        deleteFavoriteLocationUseCase(30.0, 31.0)

        // Then
        assertThat(fakeRepository.favoritesList.isEmpty(), `is`(true))
    }
}
