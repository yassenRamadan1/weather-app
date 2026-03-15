package com.example.weather_app.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.entity.user.AppLanguage
import com.example.weather_app.domain.entity.user.UserPreferences
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val application: Application,
    observeUserPreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {


    val uiPreferences = observeUserPreferences()
        .map { it.toUiPreferences() }
        .onEach { preferences ->
            application.applyNativeTheme(preferences.theme)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = run {
                val currentLocales = AppCompatDelegate.getApplicationLocales()
                val currentLangCode = currentLocales.get(0)?.language
                UiPreferences(isLoading = true)
                UserPreferences(
                    language = AppLanguage.fromCode(currentLangCode)
                ).toUiPreferences()
            },
        )
}