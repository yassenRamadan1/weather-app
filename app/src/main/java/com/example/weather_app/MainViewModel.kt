package com.example.weather_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.entity.UserPreferences
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    observeUserPreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {


    val uiPreferences = observeUserPreferences()
        .map { it.toUiPreferences() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserPreferences().toUiPreferences(),
        )
}