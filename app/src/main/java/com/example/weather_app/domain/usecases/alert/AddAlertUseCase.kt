package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.repository.WeatherRepository

class AddAlertUseCase(
    private val weatherRepository: WeatherRepository,
    private val validateAlert: ValidateAlertUseCase
) {
    suspend operator fun invoke(alert: WeatherAlert): Long {
        validateAlert(alert)
        return weatherRepository.addAlert(alert)
    }
}