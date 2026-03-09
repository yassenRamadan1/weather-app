package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.AppTheme
import com.example.weather_app.domain.repository.UserPreferencesRepository

class UpdateThemeUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(theme: AppTheme) =
        userPreferencesRepository.updateTheme(theme)
}