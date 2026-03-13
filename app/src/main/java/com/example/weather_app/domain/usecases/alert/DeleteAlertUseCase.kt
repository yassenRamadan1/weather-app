package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.service.AlarmScheduler

class DeleteAlertUseCase(
    private val weatherRepository: WeatherRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(id: Long) {
        alarmScheduler.cancelAlert(id)
        weatherRepository.deleteAlert(id)
    }
}