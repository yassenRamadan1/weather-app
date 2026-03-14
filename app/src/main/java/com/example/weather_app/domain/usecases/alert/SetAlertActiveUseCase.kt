package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.service.AlarmScheduler

class SetAlertActiveUseCase(
    private val weatherRepository: WeatherRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(id: Long, isActive: Boolean) {
        weatherRepository.setAlertActive(id, isActive)
        if (isActive) {
            val alert = weatherRepository.getAlertById(id) ?: return
            val now = System.currentTimeMillis()
            if (alert.endTimeMillis > now) {
                val triggerAt = maxOf(alert.startTimeMillis, now + 30_000L)
                alarmScheduler.scheduleAlert(id, triggerAt)
                alarmScheduler.scheduleAlertEnd(id, alert.endTimeMillis)
            }
        } else {
            alarmScheduler.cancelAlert(id)
        }
    }
}