package com.example.weather_app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.datasource.UserPreferencesDataSource
import com.example.weather_app.domain.entity.LocationResult
import com.example.weather_app.domain.entity.LocationSource
import com.example.weather_app.domain.entity.UserPreferences
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.domain.error.toUiMessage
import com.example.weather_app.domain.usecases.GetPreferredLocationUseCase
import com.example.weather_app.domain.usecases.GetWeatherUseCase
import com.example.weather_app.domain.usecases.UpdateSavedLocationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPreferredLocationUseCase: GetPreferredLocationUseCase,
    private val updateSavedLocationUseCase: UpdateSavedLocationUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage = _userMessage.asSharedFlow()
    private val _hasLaunchedPermissionRequest = MutableStateFlow(false)
    val hasLaunchedPermissionRequest: StateFlow<Boolean> =
        _hasLaunchedPermissionRequest.asStateFlow()

    fun onPermissionRequestLaunched() {
        _hasLaunchedPermissionRequest.value = true
    }

    fun resolveLocationAndLoadWeather() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            when (val result = getPreferredLocationUseCase()) {
                is LocationResult.Success -> loadWeather(
                    lat = result.lat,
                    lon = result.lon,
                    isStale = result.isStale,
                    source = result.source
                )
                is LocationResult.NeedPermission ->
                    _uiState.value = HomeUiState.NeedLocationPermission
                is LocationResult.GpsDisabled ->
                    _uiState.value = HomeUiState.GpsDisabled
                is LocationResult.NoSavedLocation ->
                    _uiState.value = HomeUiState.NeedManualLocation
                is LocationResult.Error ->
                    _uiState.value = HomeUiState.Error(
                        result.cause.message ?: "Location error"
                    )
            }
        }
    }

    fun onPermissionResult(granted: Boolean, isPermanentlyDenied: Boolean) {
        viewModelScope.launch {
            when {
                granted -> resolveLocationAndLoadWeather()

                isPermanentlyDenied -> {
                    _uiState.value = HomeUiState.NeedLocationPermission
                    _userMessage.emit(
                        "Location is permanently blocked. Please enable it in App Settings."
                    )
                }

                else -> {
                    _uiState.value = HomeUiState.NeedLocationPermission
                    _userMessage.emit(
                        "Location permission is required to show your local weather."
                    )
                }
            }
        }
    }

    fun onManualLocationSaved(lat: Double, lon: Double) {
        viewModelScope.launch {
            updateSavedLocationUseCase(lat, lon)
            loadWeather(lat, lon, isStale = false)
        }
    }

    private fun loadWeather(
        lat: Double,
        lon: Double,
        isStale: Boolean = false,
        source: LocationSource = LocationSource.GPS
    ) {
        viewModelScope.launch {
            getWeatherUseCase(lat, lon).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = HomeUiState.Success(
                            weather = it,
                            isStaleLocation = isStale,
                            locationSource = source
                        )
                    },
                    onFailure = { error ->
                        val appError = error as? AppError ?: AppError.UnknownError()
                        _uiState.value = HomeUiState.Error(appError.toUiMessage())
                    }
                )
            }
        }
    }
}