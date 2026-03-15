package com.example.weather_app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weather_app.data.notification.AlertNotificationManager
import com.example.weather_app.data.services.AlarmSchedulerImpl
import com.example.weather_app.data.services.AlertCheckWorker
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class AlertBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        const val ACTION_TRIGGER   = "com.example.weather_app.ACTION_ALERT_TRIGGER"
        const val ACTION_END       = "com.example.weather_app.ACTION_ALERT_END"
        const val EXTRA_ALERT_ID   = "extra_alert_id"
    }

    private val weatherRepository: WeatherRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getLongExtra(EXTRA_ALERT_ID, -1L)
        if (alertId == -1L) return

        when (intent.action) {
            ACTION_TRIGGER -> {
                enqueueCheckWorker(context, alertId)
                handleRescheduling(context, alertId, isTrigger = true)
            }
            ACTION_END     -> {
                AlertNotificationManager.cancelNotification(context, alertId)
                handleRescheduling(context, alertId, isTrigger = false)
            }
        }
    }

    private fun handleRescheduling(context: Context, alertId: Long, isTrigger: Boolean) {
        scope.launch {
            val alert = weatherRepository.getAlertById(alertId) ?: return@launch
            if (!alert.isRepeated || !alert.isActive) return@launch

            val scheduler = AlarmSchedulerImpl(context)
            val dayMillis = 24 * 60 * 60 * 1000L

            if (isTrigger) {
                val nextStart = alert.startTimeMillis + dayMillis
                scheduler.scheduleAlert(alertId, nextStart)
                weatherRepository.addAlert(alert.copy(startTimeMillis = nextStart))
            } else {
                val nextEnd = alert.endTimeMillis + dayMillis
                scheduler.scheduleAlertEnd(alertId, nextEnd)
                weatherRepository.addAlert(alert.copy(endTimeMillis = nextEnd))
            }
        }
    }

    private fun enqueueCheckWorker(context: Context, alertId: Long) {
        val inputData = Data.Builder()
            .putLong(AlertCheckWorker.KEY_ALERT_ID, alertId)
            .build()

        val request = OneTimeWorkRequestBuilder<AlertCheckWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(
                "alert_check_$alertId",
                androidx.work.ExistingWorkPolicy.KEEP,
                request
            )
            .enqueue()
    }
}