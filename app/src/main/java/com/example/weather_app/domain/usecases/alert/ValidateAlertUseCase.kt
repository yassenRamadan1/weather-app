package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.error.AppError

class ValidateAlertUseCase {

    companion object {
        private const val MIN_START_OFFSET_MS = 30_000L
    }

    operator fun invoke(alert: WeatherAlert) {
        val now = System.currentTimeMillis()
        if (alert.startTimeMillis < now + MIN_START_OFFSET_MS) {
            throw AppError.InvalidPeriod
        }
        if (alert.endTimeMillis <= alert.startTimeMillis) {
            throw AppError.PeriodOrder
        }
        if (alert.conditionMode == AlertConditionMode.CONDITIONS) {
            val hasAnyThreshold = alert.temperatureThreshold != null
                    || alert.windThreshold != null
                    || alert.cloudinessThreshold != null

            if (!hasAnyThreshold) throw AppError.NoDaysSelected

            alert.windThreshold?.let {
                if (it < 0.0) throw AppError.ThresholdNeg(it)
            }
            alert.cloudinessThreshold?.let {
                if (it < 0) throw AppError.ThresholdNeg(it.toDouble())
            }
        }
    }
}