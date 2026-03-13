package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.service.AlarmScheduler

class AddAlertUseCase(
    private val weatherRepository: WeatherRepository,
    private val validateAlert: ValidateAlertUseCase,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alert: WeatherAlert): Long {
        validateAlert(alert)
        val id = weatherRepository.addAlert(alert)
        alarmScheduler.scheduleAlert(id, alert.startTimeMillis)
        alarmScheduler.scheduleAlertEnd(id, alert.endTimeMillis)
        return id
    }
}