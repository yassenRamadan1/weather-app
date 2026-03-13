package com.example.weather_app.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.R
import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.entity.user.LocationResult
import com.example.weather_app.domain.error.AppError
import com.example.weather_app.domain.repository.UserPreferencesRepository
import com.example.weather_app.domain.service.AlarmScheduler
import com.example.weather_app.domain.usecases.alert.AddAlertUseCase
import com.example.weather_app.domain.usecases.alert.DeleteAlertUseCase
import com.example.weather_app.domain.usecases.alert.GetAllAlertsUseCase
import com.example.weather_app.domain.usecases.alert.SetAlertActiveUseCase
import com.example.weather_app.presentation.alerts.components.bottomsheet.AddAlertFormState
import com.example.weather_app.presentation.uierror.UiText
import com.example.weather_app.presentation.uierror.toUiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertsScreenViewModel(
    private val getAllAlerts: GetAllAlertsUseCase,
    private val addAlertUseCase: AddAlertUseCase,
    private val deleteAlertUseCase: DeleteAlertUseCase,
    private val setAlertActiveUseCase: SetAlertActiveUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlertsScreenUiState>(AlertsScreenUiState.Loading)
    val uiState: StateFlow<AlertsScreenUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AlertsScreenEffect>()
    val effect: SharedFlow<AlertsScreenEffect> = _effect.asSharedFlow()

    private val _formState = MutableStateFlow(AddAlertFormState())
    val formState: StateFlow<AddAlertFormState> = _formState.asStateFlow()

    init {
        observeAlerts()
    }

    private fun observeAlerts() {
        viewModelScope.launch {
            try {
                getAllAlerts().collect { alerts ->
                    _uiState.value = if (alerts.isEmpty()) AlertsScreenUiState.Empty
                    else AlertsScreenUiState.Success(alerts)
                }
            } catch (e: Exception) {
                val error = e as? AppError ?: AppError.UnknownError()
                _uiState.value = AlertsScreenUiState.Error(error.toUiText())
            }
        }
    }

    fun onStartTimeSelected(millis: Long) {
        val now = System.currentTimeMillis()
        val error = if (millis < now + 30_000L)
            UiText.StringResource(R.string.error_start_time_future) else null
        _formState.update { it.copy(startTimeMillis = millis, startError = error) }
        _formState.value.endTimeMillis?.let { end ->
            if (end <= millis) {
                _formState.update {
                    it.copy(endError = UiText.StringResource(R.string.error_end_after_start))
                }
            } else if (_formState.value.endError?.equals(
                    UiText.StringResource(R.string.error_end_after_start)
                ) == true
            ) {
                _formState.update { it.copy(endError = null) }
            }
        }
    }

    fun onEndTimeSelected(millis: Long) {
        val now = System.currentTimeMillis()
        val start = _formState.value.startTimeMillis
        val error = when {
            millis <= now -> UiText.StringResource(R.string.error_end_time_future)
            start != null && millis <= start -> UiText.StringResource(R.string.error_end_after_start)
            else -> null
        }
        _formState.update { it.copy(endTimeMillis = millis, endError = error) }
    }

    fun onAlertTypeChanged(type: AlertType) =
        _formState.update { it.copy(alertType = type) }

    fun onConditionModeChanged(mode: AlertConditionMode) =
        _formState.update { it.copy(conditionMode = mode, conditionError = null) }

    fun onTemperatureThresholdChanged(value: String) =
        _formState.update { it.copy(temperatureThreshold = value, conditionError = null) }

    fun onWindThresholdChanged(value: String) =
        _formState.update { it.copy(windThreshold = value, conditionError = null) }

    fun onCloudinessThresholdChanged(value: String) {
        // Clamp cloudiness input to 0–100
        val clamped = value.toIntOrNull()?.coerceIn(0, 100)?.toString() ?: value
        _formState.update { it.copy(cloudinessThreshold = clamped, conditionError = null) }
    }

    fun resetForm() {
        _formState.value = AddAlertFormState()
    }
    fun saveAlert() {
        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            try {
                val prefs = userPreferencesRepository.userPreferences.first()
                val locResult = userPreferencesRepository.getPreferredLocation(prefs)

                if (locResult !is LocationResult.Success) {
                    _effect.emit(
                        AlertsScreenEffect.ShowSnackbar(
                            UiText.StringResource(R.string.error_no_location)
                        )
                    )
                    return@launch
                }

                val form = _formState.value
                val alert = WeatherAlert(
                    startTimeMillis = form.startTimeMillis!!,
                    endTimeMillis = form.endTimeMillis!!,
                    alertType = form.alertType,
                    conditionMode = form.conditionMode,
                    temperatureThreshold = form.temperatureThreshold.toDoubleOrNull(),
                    windThreshold = form.windThreshold.toDoubleOrNull(),
                    cloudinessThreshold = form.cloudinessThreshold.toIntOrNull(),
                    isActive = true,
                    lat = locResult.lat,
                    lon = locResult.lon,
                    cityName = locResult.cityName
                )

                val alertId = addAlertUseCase(alert)
                alarmScheduler.scheduleAlert(alertId, alert.startTimeMillis)
                alarmScheduler.scheduleAlertEnd(alertId, alert.endTimeMillis)
                resetForm()

            } catch (e: AppError.InvalidPeriod) {
                _formState.update {
                    it.copy(startError = UiText.StringResource(R.string.error_start_time_future))
                }
            } catch (e: AppError.PeriodOrder) {
                _formState.update {
                    it.copy(endError = UiText.StringResource(R.string.error_end_after_start))
                }
            } catch (e: AppError.NoDaysSelected) {
                _formState.update {
                    it.copy(conditionError = UiText.StringResource(R.string.error_no_condition_set))
                }
            } catch (e: AppError.ThresholdNeg) {
                _formState.update {
                    it.copy(conditionError = UiText.StringResource(R.string.error_threshold_negative))
                }
            } catch (e: Exception) {
                val appError = e as? AppError ?: AppError.UnknownError()
                _effect.emit(AlertsScreenEffect.ShowSnackbar(appError.toUiText()))
            } finally {
                _formState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun toggleAlertActive(alertId: Long, currentlyActive: Boolean) {
        viewModelScope.launch {
            try {
                val newActive = !currentlyActive
                setAlertActiveUseCase(alertId, newActive)

                if (newActive) {
                    val alert = findAlertById(alertId) ?: return@launch
                    val now = System.currentTimeMillis()
                    if (alert.endTimeMillis > now) {
                        val triggerAt = maxOf(alert.startTimeMillis, now + 30_000L)
                        alarmScheduler.scheduleAlert(alertId, triggerAt)
                        alarmScheduler.scheduleAlertEnd(alertId, alert.endTimeMillis)
                    }
                } else {
                    alarmScheduler.cancelAlert(alertId)
                }
            } catch (e: Exception) {
                val appError = e as? AppError ?: AppError.UnknownError()
                _effect.emit(AlertsScreenEffect.ShowSnackbar(appError.toUiText()))
            }
        }
    }

    fun deleteAlert(alertId: Long) {
        viewModelScope.launch {
            try {
                alarmScheduler.cancelAlert(alertId)
                deleteAlertUseCase(alertId)
            } catch (e: Exception) {
                val appError = e as? AppError ?: AppError.UnknownError()
                _effect.emit(AlertsScreenEffect.ShowSnackbar(appError.toUiText()))
            }
        }
    }


    private fun findAlertById(id: Long): WeatherAlert? =
        (_uiState.value as? AlertsScreenUiState.Success)?.alerts?.find { it.id == id }
}