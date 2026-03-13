package com.example.weather_app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.R
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.user.LocationResult
import com.example.weather_app.domain.entity.user.LocationSource
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.presentation.uierror.UiText
import com.example.weather_app.presentation.uierror.toUiText
import com.example.weather_app.domain.usecases.GetDailyForecastUseCase
import com.example.weather_app.domain.usecases.GetHourlyForecastUseCase
import com.example.weather_app.domain.usecases.GetPreferredLocationUseCase
import com.example.weather_app.domain.usecases.GetWeatherUseCase
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel(
    private val getPreferredLocationUseCase: GetPreferredLocationUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
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

    private var locationResolutionJob: Job? = null

    fun onPermissionRequestLaunched() {
        _hasLaunchedPermissionRequest.value = true
    }

    fun resolveLocationAndLoadWeather() {
        locationResolutionJob?.cancel()
        locationResolutionJob = viewModelScope.launch {
            if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Loading
            }
            resolveAndLoad()
        }
    }

    fun onRefresh() {
        locationResolutionJob?.cancel()
        locationResolutionJob = viewModelScope.launch {
            _isRefreshing.value = true
            resolveAndLoad()
            _isRefreshing.value = false
        }
    }

    private suspend fun resolveAndLoad() {
        when (val result = getPreferredLocationUseCase()) {
            is LocationResult.Success -> loadWeatherData(
                lat = result.lat,
                lon = result.lon,
                isStale = result.isStale,
                source = result.source
            )
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

    private suspend fun loadWeatherData(
        lat: Double,
        lon: Double,
        isStale: Boolean = false,
        source: LocationSource = LocationSource.GPS
    ) {
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
            _uiState.value = HomeUiState.Success(
                currentWeather = weather,
                hourlyForecast = latestHourly,
                dailyForecast = latestDaily,
                isStaleLocation = isStale,
                locationSource = source,
                currentDateFormatted = dateFormatted,
                currentTimeFormatted = timeFormatted,
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
                                _uiState.value = HomeUiState.Error(appError.toUiText())
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
