package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.service.AlarmScheduler

class SetAlertActiveUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(id: Long, isActive: Boolean) =
        weatherRepository.setAlertActive(id, isActive)
}