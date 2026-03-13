package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.user.AppLanguage
import com.example.weather_app.domain.repository.UserPreferencesRepository

class UpdateLanguageUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(language: AppLanguage) =
        userPreferencesRepository.updateLanguage(language)
}