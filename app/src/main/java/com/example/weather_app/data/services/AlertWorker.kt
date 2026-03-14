package com.example.weather_app.data.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather_app.data.notification.AlertNotificationManager
import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlertCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    companion object {
        const val KEY_ALERT_ID = "key_alert_id"
    }

    private val weatherRepository: WeatherRepository by inject()

    override suspend fun doWork(): Result {
        val alertId = inputData.getLong(KEY_ALERT_ID, -1L)
        if (alertId == -1L) return Result.failure()
        val activeAlerts = weatherRepository.getActiveAlerts()
        val alert = activeAlerts.find { it.id == alertId }
            ?: return Result.success()

        val now = System.currentTimeMillis()
        if (now > alert.endTimeMillis) {
            AlertNotificationManager.cancelNotification(applicationContext, alertId)
            return Result.success()
        }

        val weatherResult = runCatching {
            weatherRepository.getCurrentWeather(alert.lat, alert.lon)
                .first { it.isSuccess || it.isFailure }
        }.getOrElse { return Result.retry() }

        val weather = weatherResult.getOrNull() ?: return Result.retry()

        val shouldNotify = when (alert.conditionMode) {
            AlertConditionMode.ANY        -> true
            AlertConditionMode.CONDITIONS -> meetsConditions(alert, weather)
        }

        if (shouldNotify) {
            when (alert.alertType) {
                AlertType.NOTIFICATION ->
                    AlertNotificationManager.showNotification(applicationContext, alert, weather)
                AlertType.ALARM ->
                    AlertNotificationManager.showAlarm(applicationContext, alert, weather)
            }
        }

        return Result.success()
    }

    private fun meetsConditions(alert: WeatherAlert, weather: Weather): Boolean {
        alert.temperatureThreshold?.let { if (weather.temperature >= it) return true }
        alert.windThreshold?.let { if (weather.windSpeed >= it) return true }
        alert.cloudinessThreshold?.let { if (weather.cloudiness >= it) return true }
        return false
    }
}