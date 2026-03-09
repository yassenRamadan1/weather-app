package com.example.weather_app

import com.example.weather_app.domain.entity.AppLanguage
import com.example.weather_app.domain.entity.AppTheme
import com.example.weather_app.domain.entity.UserPreferences

data class UiPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
)

 fun UserPreferences.toUiPreferences() = UiPreferences(
    theme = theme,
    language = language,
)