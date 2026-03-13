package com.example.weather_app.domain.service


interface AlarmScheduler {
    fun scheduleAlert(alertId: Long, triggerAtMillis: Long)

    fun scheduleAlertEnd(alertId: Long, endAtMillis: Long)

    fun cancelAlert(alertId: Long)
}