package com.example.weather_app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.R
import com.example.weather_app.domain.entity.user.LocationResult
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.domain.usecases.GetDailyForecastUseCase
import com.example.weather_app.domain.usecases.GetHourlyForecastUseCase
import com.example.weather_app.domain.usecases.GetPreferredLocationUseCase
import com.example.weather_app.domain.usecases.GetWeatherUseCase
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import com.example.weather_app.presentation.uierror.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPreferredLocationUseCase: GetPreferredLocationUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val uiMapper: HomeUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _userMessage = MutableSharedFlow<Int>()
    val userMessage = _userMessage.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _hasLaunchedPermissionRequest = MutableStateFlow(false)
    val hasLaunchedPermissionRequest: StateFlow<Boolean> =
        _hasLaunchedPermissionRequest.asStateFlow()

    private var weatherLoadingJob: Job? = null

    fun onPermissionRequestLaunched() {
        _hasLaunchedPermissionRequest.value = true
    }

    fun resolveLocationAndLoadWeather() {
        viewModelScope.launch {
            if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Loading
            }
            fetchLocationAndWeather()
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchLocationAndWeather()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchLocationAndWeather() {
        when (val result = getPreferredLocationUseCase()) {
            is LocationResult.Success -> {
                observeWeatherData(result)
            }
            is LocationResult.NeedPermission ->
                _uiState.value = HomeUiState.NeedLocationPermission
            is LocationResult.GpsDisabled ->
                _uiState.value = HomeUiState.GpsDisabled
            is LocationResult.GpsNoFix ->
                _uiState.value = HomeUiState.GpsNoFix
            is LocationResult.NoSavedLocation ->
                _uiState.value = HomeUiState.NeedManualLocation
            is LocationResult.Error -> {
                val appError = result.cause as? AppError ?: AppError.UnknownError()
                _uiState.value = HomeUiState.Error(appError.toUiText())
            }
        }
    }

    private fun observeWeatherData(location: LocationResult.Success) {
        weatherLoadingJob?.cancel()
        weatherLoadingJob = combine(
            getWeatherUseCase(location.lat, location.lon),
            getHourlyForecastUseCase(location.lat, location.lon),
            getDailyForecastUseCase(location.lat, location.lon),
            observeUserPreferencesUseCase()
        ) { weatherRes, hourlyRes, dailyRes, prefs ->
            
            val weather = weatherRes.getOrNull()
            val hourly = hourlyRes.getOrNull() ?: emptyList()
            val daily = dailyRes.getOrNull() ?: emptyList()

            if (weather != null) {
                uiMapper.mapToSuccess(
                    weather = weather,
                    hourly = hourly,
                    daily = daily,
                    prefs = prefs,
                    isStale = location.isStale,
                    source = location.source,
                    isFromCache = weatherRes.isFailure
                )
            } else if (weatherRes.isFailure) {
                val appError = weatherRes.exceptionOrNull() as? AppError ?: AppError.UnknownError()
                HomeUiState.Error(appError.toUiText())
            } else {
                HomeUiState.Loading
            }
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    fun onPermissionResult(granted: Boolean, isPermanentlyDenied: Boolean) {
        viewModelScope.launch {
            when {
                granted -> resolveLocationAndLoadWeather()
                isPermanentlyDenied -> {
                    _uiState.value = HomeUiState.NeedLocationPermission
                    _userMessage.emit(R.string.msg_location_permanently_denied)
                }
                else -> {
                    _uiState.value = HomeUiState.NeedLocationPermission
                    _userMessage.emit(R.string.msg_permission_required)
                }
            }
        }
    }
}
