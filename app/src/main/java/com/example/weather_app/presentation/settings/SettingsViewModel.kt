package com.example.weather_app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // ── One-shot events ───────────────────────────────────────────────────────
    // Channel (not SharedFlow) to guarantee exactly-once delivery of UI events.

    private val _events = Channel<SettingsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // ── GPS ───────────────────────────────────────────────────────────────────

    /** Called from the UI on every ON_RESUME lifecycle event. */
    fun refreshGpsState() {
        _isGpsEnabled.update { locationProvider.isLocationServicesEnabled() }
    }

    // ── Appearance ────────────────────────────────────────────────────────────

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        updateThemeUseCase(theme)
    }

    // ── Language ──────────────────────────────────────────────────────────────

    fun setLanguage(language: AppLanguage) = viewModelScope.launch {
        updateLanguageUseCase(language)
    }

    // ── Units ─────────────────────────────────────────────────────────────────

    fun setTemperatureUnit(unit: TemperatureUnit) = viewModelScope.launch {
        updateTemperatureUnitUseCase(unit)
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) = viewModelScope.launch {
        updateWindSpeedUnitUseCase(unit)
    }

    // ── Location ──────────────────────────────────────────────────────────────

    fun setLocationMode(mode: LocationMode) = viewModelScope.launch {
        updateLocationModeUseCase(mode)
        if (mode == LocationMode.GPS && !_isGpsEnabled.value) {
            _events.send(SettingsEvent.ShowMessage(MSG_GPS_OFF))
        }
    }

    /**
     * Called after the system permission dialog resolves.
     * Handles both grant and denial, including the GPS-enabled check on grant.
     */
    fun onPermissionResult(granted: Boolean) = viewModelScope.launch {
        if (granted) {
            updateLocationModeUseCase(LocationMode.GPS)
            if (!_isGpsEnabled.value) {
                _events.send(SettingsEvent.ShowMessage(MSG_PERMISSION_GRANTED_GPS_OFF))
            }
        } else {
            _events.send(SettingsEvent.ShowMessage(MSG_PERMISSION_DENIED))
        }
    }

    /**
     * Persists the manually picked coordinates and switches mode to MAP.
     * Both writes are batched in a single use-case sequence so the state
     * is never partially applied.
     */
    fun onManualLocationSaved(lat: Double, lon: Double) = viewModelScope.launch {
        updateSavedLocationUseCase(lat, lon)
        updateLocationModeUseCase(LocationMode.MAP)
    }

    // ── Message constants ─────────────────────────────────────────────────────

    private companion object {
        const val MSG_GPS_OFF =
            "GPS is off — showing last known location. Enable GPS for live updates."
        const val MSG_PERMISSION_GRANTED_GPS_OFF =
            "Permission granted. Please also enable GPS in device settings."
        const val MSG_PERMISSION_DENIED =
            "Location permission denied — staying on current mode."
    }
}