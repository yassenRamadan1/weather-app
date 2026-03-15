package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.service.AlarmScheduler

class FakeAlarmScheduler : AlarmScheduler {
    val scheduledAlerts = mutableMapOf<Long, Long>()
    val cancelledAlerts = mutableListOf<Long>()

    override fun scheduleAlert(alertId: Long, triggerAtMillis: Long) {
        scheduledAlerts[alertId] = triggerAtMillis
    }

    override fun scheduleAlertEnd(alertId: Long, endAtMillis: Long) {
        // Track if needed
    }

    override fun cancelAlert(alertId: Long) {
        cancelledAlerts.add(alertId)
        scheduledAlerts.remove(alertId)
    }
}
