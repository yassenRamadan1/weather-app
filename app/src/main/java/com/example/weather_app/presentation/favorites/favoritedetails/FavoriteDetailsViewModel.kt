package com.example.weather_app.presentation.favorites.favoritedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.entity.HourlyWeather
import com.example.weather_app.domain.entity.Weather
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.domain.error.toUiMessage
import com.example.weather_app.domain.usecases.GetDailyForecastUseCase
import com.example.weather_app.domain.usecases.GetHourlyForecastUseCase
import com.example.weather_app.domain.usecases.GetWeatherUseCase
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import com.example.weather_app.navigation.Screen
import com.example.weather_app.presentation.favorites.favorite.FavoritesScreenUiState
import com.example.weather_app.presentation.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class FavoriteDetailsViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<FavoriteDetailsScreenUiState>(FavoriteDetailsScreenUiState.Loading)
    val uiState: StateFlow<FavoriteDetailsScreenUiState> = _uiState.asStateFlow()
    private val _effect = MutableStateFlow<FavoriteDetailsEffect?>(null)
    val effect: StateFlow<FavoriteDetailsEffect?> = _effect.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val lat: Double = checkNotNull(
        savedStateHandle.get<String>(Screen.FavoriteDetails.ARG_LAT)?.toDoubleOrNull()
    ) { "lat argument is missing or invalid" }

    private val lon: Double = checkNotNull(
        savedStateHandle.get<String>(Screen.FavoriteDetails.ARG_LON)?.toDoubleOrNull()
    ) { "lon argument is missing or invalid" }

    init {
        viewModelScope.launch {
            loadWeatherForLocation(lat, lon)
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadWeatherForLocation(lat, lon)
            _isRefreshing.value = false
        }
    }

    private suspend fun loadWeatherForLocation(lat: Double, lon: Double) {
        val prefs = observeUserPreferencesUseCase().first()
        val now = Instant.now().atZone(ZoneId.systemDefault())
        val locale = Locale(prefs.language.code)
        val dateFormatted = now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
        val timeFormatted = now.format(DateTimeFormatter.ofPattern("HH:mm", locale))
        var latestWeather: Weather? = null
        var latestHourly: List<HourlyWeather> = emptyList()
        var latestDaily: List<DailyForecast> = emptyList()
        var networkFailed = false

        fun buildSuccessState() {
            val weather = latestWeather ?: return
            _uiState.value = FavoriteDetailsScreenUiState.Success(
                currentWeather = weather,
                hourlyForecast = latestHourly,
                dailyForecast = latestDaily,
                currentTimeFormatted = timeFormatted,
                currentDateFormatted = dateFormatted,
                temperatureUnit = prefs.temperatureUnit,
                windSpeedUnit = prefs.windSpeedUnit,
                isFromCache = networkFailed,
            )
        }

        viewModelScope.launch {
            launch {
                getWeatherUseCase(lat, lon).collect { result ->
                    result.fold(
                        onSuccess = { weather ->
                            latestWeather = weather
                            buildSuccessState()
                        },
                        onFailure = { error ->
                            if (latestWeather == null) {
                                val appError = error as? AppError ?: AppError.UnknownError()
                                _uiState.value =
                                    FavoriteDetailsScreenUiState.Error(appError.toUiMessage())
                            } else {
                                networkFailed = true
                                buildSuccessState()
                            }
                        }
                    )
                }
            }

            launch {
                getHourlyForecastUseCase(lat, lon).collect { result ->
                    result.onSuccess { list ->
                        latestHourly = list
                        buildSuccessState()
                    }
                }
            }

            launch {
                getDailyForecastUseCase(lat, lon).collect { result ->
                    result.onSuccess { list ->
                        latestDaily = list
                        buildSuccessState()
                    }
                }
            }
        }
    }
}