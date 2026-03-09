package com.example.weather_app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.R
import com.example.weather_app.data.location.AndroidLocationProvider
import com.example.weather_app.domain.datasource.UserPreferencesDataSource
import com.example.weather_app.domain.entity.*
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import com.example.weather_app.domain.usecases.UpdateLanguageUseCase
import com.example.weather_app.domain.usecases.UpdateLocationModeUseCase
import com.example.weather_app.domain.usecases.UpdateSavedLocationUseCase
import com.example.weather_app.domain.usecases.UpdateTemperatureUnitUseCase
import com.example.weather_app.domain.usecases.UpdateThemeUseCase
import com.example.weather_app.domain.usecases.UpdateWindSpeedUnitUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateTemperatureUnitUseCase: UpdateTemperatureUnitUseCase,
    private val updateWindSpeedUnitUseCase: UpdateWindSpeedUnitUseCase,
    private val updateLocationModeUseCase: UpdateLocationModeUseCase,
    private val updateSavedLocationUseCase: UpdateSavedLocationUseCase,
    private val locationProvider: AndroidLocationProvider,
) : ViewModel() {
    val userPreferences = observeUserPreferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences(),
        )

    private val _isGpsEnabled = MutableStateFlow(locationProvider.isLocationServicesEnabled())
    val isGpsEnabled = _isGpsEnabled.asStateFlow()


    private val _events = Channel<SettingsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun refreshGpsState() {
        _isGpsEnabled.update { locationProvider.isLocationServicesEnabled() }
    }

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        updateThemeUseCase(theme)
    }

    fun setLanguage(language: AppLanguage) = viewModelScope.launch {
        updateLanguageUseCase(language)
    }

    fun setTemperatureUnit(unit: TemperatureUnit) = viewModelScope.launch {
        updateTemperatureUnitUseCase(unit)
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) = viewModelScope.launch {
        updateWindSpeedUnitUseCase(unit)
    }

    fun setLocationMode(mode: LocationMode) = viewModelScope.launch {
        updateLocationModeUseCase(mode)
        if (mode == LocationMode.GPS && !_isGpsEnabled.value) {
            _events.send(SettingsEvent.ShowMessage(R.string.msg_gps_off))
        }
    }

    fun onPermissionResult(granted: Boolean) = viewModelScope.launch {
        if (granted) {
            updateLocationModeUseCase(LocationMode.GPS)
            if (!_isGpsEnabled.value) {
                _events.send(SettingsEvent.ShowMessage(R.string.msg_permission_granted_gps_off))
            }
        } else {
            _events.send(SettingsEvent.ShowMessage(R.string.msg_permission_denied))
        }
    }

    fun onManualLocationSaved(lat: Double, lon: Double) = viewModelScope.launch {
        updateSavedLocationUseCase(lat, lon)
        updateLocationModeUseCase(LocationMode.MAP)
    }
}