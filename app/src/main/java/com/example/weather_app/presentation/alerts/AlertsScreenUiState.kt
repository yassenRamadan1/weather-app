package com.example.weather_app.presentation.alerts

import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.presentation.uierror.UiText

sealed interface AlertsScreenUiState {
    data object Loading : AlertsScreenUiState
    data object Empty   : AlertsScreenUiState
    data class  Success(val alerts: List<WeatherAlert>) : AlertsScreenUiState
    data class  Error(val message: UiText) : AlertsScreenUiState
}