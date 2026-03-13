package com.example.weather_app.data.weather.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weather_app.data.weather.notification.AlertNotificationManager
import com.example.weather_app.data.weather.services.AlertCheckWorker


class AlertBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER   = "com.example.weather_app.ACTION_ALERT_TRIGGER"
        const val ACTION_END       = "com.example.weather_app.ACTION_ALERT_END"
        const val EXTRA_ALERT_ID   = "extra_alert_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getLongExtra(EXTRA_ALERT_ID, -1L)
        if (alertId == -1L) return

        when (intent.action) {
            ACTION_TRIGGER -> enqueueCheckWorker(context, alertId)
            ACTION_END     -> AlertNotificationManager.cancelNotification(context, alertId)
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