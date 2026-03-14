package com.example.weather_app.presentation.favorites.favoritedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.domain.usecases.GetDailyForecastUseCase
import com.example.weather_app.domain.usecases.GetHourlyForecastUseCase
import com.example.weather_app.domain.usecases.GetWeatherUseCase
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import com.example.weather_app.navigation.Screen
import com.example.weather_app.presentation.uierror.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoriteDetailsViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val uiMapper: FavoriteDetailsUiMapper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<FavoriteDetailsScreenUiState>(FavoriteDetailsScreenUiState.Loading)
    val uiState: StateFlow<FavoriteDetailsScreenUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<FavoriteDetailsEffect?>()
    val effect: SharedFlow<FavoriteDetailsEffect?> = _effect.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val lat: Double = checkNotNull(
        savedStateHandle.get<String>(Screen.FavoriteDetails.ARG_LAT)?.toDoubleOrNull()
    ) { "lat argument is missing or invalid" }

    private val lon: Double = checkNotNull(
        savedStateHandle.get<String>(Screen.FavoriteDetails.ARG_LON)?.toDoubleOrNull()
    ) { "lon argument is missing or invalid" }

    private var weatherLoadingJob: Job? = null

    init {
        observeWeatherData()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            observeWeatherData()
            _isRefreshing.value = false
        }
    }

    private fun observeWeatherData() {
        weatherLoadingJob?.cancel()
        weatherLoadingJob = combine(
            getWeatherUseCase(lat, lon),
            getHourlyForecastUseCase(lat, lon),
            getDailyForecastUseCase(lat, lon),
            observeUserPreferencesUseCase()
        ) { weatherRes, hourlyRes, dailyRes, prefs ->
            val weather = weatherRes.getOrNull()
            val hourly = hourlyRes.getOrNull()
            val daily = dailyRes.getOrNull()

            val currentState = _uiState.value

            if (weather != null && hourly != null && daily != null) {
                uiMapper.mapToSuccess(
                    weather = weather,
                    hourly = hourly,
                    daily = daily,
                    prefs = prefs,
                    isFromCache = false
                )
            } else if (weatherRes.isFailure || hourlyRes.isFailure || dailyRes.isFailure) {
                if (currentState is FavoriteDetailsScreenUiState.Success) {
                    currentState.copy(isFromCache = true)
                } else {
                    val appError = (weatherRes.exceptionOrNull()
                        ?: hourlyRes.exceptionOrNull()
                        ?: dailyRes.exceptionOrNull()) as? AppError ?: AppError.UnknownError()
                    FavoriteDetailsScreenUiState.Error(appError.toUiText())
                }
            } else {
                if (currentState is FavoriteDetailsScreenUiState.Success) currentState else FavoriteDetailsScreenUiState.Loading
            }
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }
}
