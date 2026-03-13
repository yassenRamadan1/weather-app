package com.example.weather_app.main

import com.example.weather_app.domain.entity.user.AppLanguage
import com.example.weather_app.domain.entity.user.AppTheme
import com.example.weather_app.domain.entity.user.UserPreferences

data class UiPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
)

 fun UserPreferences.toUiPreferences() = UiPreferences(
    theme = theme,
    language = language,
)