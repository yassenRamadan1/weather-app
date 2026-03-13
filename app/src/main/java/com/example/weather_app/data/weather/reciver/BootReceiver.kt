package com.example.weather_app.data.weather.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weather_app.data.weather.services.AlarmSchedulerImpl
import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.service.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val weatherRepository: WeatherRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val scheduler = AlarmSchedulerImpl(context)
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope.launch {
            val now = System.currentTimeMillis()
            try {
                weatherRepository.getActiveAlerts().forEach { alert ->
                    if (alert.endTimeMillis <= now) return@forEach
                    val triggerAt = if (alert.startTimeMillis > now) {
                        alert.startTimeMillis
                    } else {
                        now + 30_000L
                    }

                    scheduler.scheduleAlert(alert.id, triggerAt)
                    scheduler.scheduleAlertEnd(alert.id, alert.endTimeMillis)
                }
            } catch (e: Exception) {
            }
        }
    }
}