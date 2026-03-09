package com.example.weather_app.presentation.settings

import androidx.annotation.StringRes

sealed interface SettingsEvent {
    data class ShowMessage(@StringRes val messageResId: Int) : SettingsEvent
}