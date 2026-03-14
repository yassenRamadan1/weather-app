package com.example.weather_app.data.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.weather_app.data.receiver.AlertBroadcastReceiver
import com.example.weather_app.domain.service.AlarmScheduler

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    override fun scheduleAlert(alertId: Long, triggerAtMillis: Long) {
        val pi = buildPendingIntent(alertId, AlertBroadcastReceiver.ACTION_TRIGGER, alertId.toInt())
        scheduleExact(triggerAtMillis, pi)
    }

    override fun scheduleAlertEnd(alertId: Long, endAtMillis: Long) {
        val pi = buildPendingIntent(alertId, AlertBroadcastReceiver.ACTION_END, endRequestCode(alertId))
        scheduleExact(endAtMillis, pi)
    }

    override fun cancelAlert(alertId: Long) {
        cancelPendingIntent(alertId, AlertBroadcastReceiver.ACTION_TRIGGER, alertId.toInt())
        cancelPendingIntent(alertId, AlertBroadcastReceiver.ACTION_END, endRequestCode(alertId))
    }

    private fun scheduleExact(triggerAtMillis: Long, pi: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        }
    }

    private fun buildPendingIntent(alertId: Long, action: String, requestCode: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            buildIntent(alertId, action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun cancelPendingIntent(alertId: Long, action: String, requestCode: Int) {
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            buildIntent(alertId, action),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pi?.let { alarmManager.cancel(it) }
    }

    private fun buildIntent(alertId: Long, action: String): Intent =
        Intent(context, AlertBroadcastReceiver::class.java).apply {
            this.action = action
            putExtra(AlertBroadcastReceiver.EXTRA_ALERT_ID, alertId)
        }

    private fun endRequestCode(alertId: Long): Int = (alertId + 100_000L).toInt()
}