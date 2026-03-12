package com.example.weather_app.main

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.entity.AppLanguage
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
            initialValue = run {
                val currentLocales = AppCompatDelegate.getApplicationLocales()
                val currentLangCode = currentLocales.get(0)?.language
                UserPreferences(
                    language = AppLanguage.fromCode(currentLangCode)
                ).toUiPreferences()
            },
        )
}