package com.example.weather_app.presentation.settings

sealed interface SettingsEvent {
    data class ShowMessage(val message: String) : SettingsEvent
}