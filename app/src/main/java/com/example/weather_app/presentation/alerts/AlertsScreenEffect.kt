package com.example.weather_app.presentation.alerts

import com.example.weather_app.presentation.uierror.UiText

sealed interface AlertsScreenEffect {
    data class ShowSnackbar(val message: UiText) : AlertsScreenEffect
    data object RequestNotificationPermission : AlertsScreenEffect
    data object RequestExactAlarmPermission   : AlertsScreenEffect
    data object SaveSuccess : AlertsScreenEffect
}