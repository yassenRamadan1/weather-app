package com.example.weather_app.data.weather.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weather_app.MainActivity
import com.example.weather_app.R
import com.example.weather_app.data.weather.reciver.AlertBroadcastReceiver
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.entity.weather.Weather

object AlertNotificationManager {

    const val CHANNEL_NOTIFICATION = "weather_alerts_notification"
    const val CHANNEL_ALARM        = "weather_alerts_alarm"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)
            val notifChannel = NotificationChannel(
                CHANNEL_NOTIFICATION,
                context.getString(R.string.channel_notification_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_notification_desc)
            }

            val alarmChannel = NotificationChannel(
                CHANNEL_ALARM,
                context.getString(R.string.channel_alarm_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_alarm_desc)
                val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val audioAttr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(alarmUri, audioAttr)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 300, 500, 300, 500)
            }

            nm.createNotificationChannel(notifChannel)
            nm.createNotificationChannel(alarmChannel)
        }
    }

    fun showNotification(context: Context, alert: WeatherAlert, weather: Weather) {
        val nm = context.getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(context, CHANNEL_NOTIFICATION)
            .setSmallIcon(R.drawable.cloudy_logo)
            .setContentTitle(buildTitle(context, alert))
            .setContentText(buildWeatherSummary(weather))
            .setStyle(NotificationCompat.BigTextStyle().bigText(buildWeatherSummary(weather)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(buildContentIntent(context, alert.id))
            .build()
        nm.notify(alert.id.toInt(), notification)
    }


    fun showAlarm(context: Context, alert: WeatherAlert, weather: Weather) {
        val nm = context.getSystemService(NotificationManager::class.java)
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val notification = NotificationCompat.Builder(context, CHANNEL_ALARM)
            .setSmallIcon(R.drawable.cloudy_logo)
            .setContentTitle(buildTitle(context, alert))
            .setContentText(buildWeatherSummary(weather))
            .setStyle(NotificationCompat.BigTextStyle().bigText(buildWeatherSummary(weather)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmUri)
            .setVibrate(longArrayOf(0, 500, 300, 500, 300, 500))
            .setAutoCancel(true)
            .setContentIntent(buildContentIntent(context, alert.id))
            .addAction(
                R.drawable.cloudy_logo,
                context.getString(R.string.action_dismiss),
                buildDismissIntent(context, alert.id)
            )
            .build()
        nm.notify(alert.id.toInt(), notification)
    }

    fun cancelNotification(context: Context, alertId: Long) {
        context.getSystemService(NotificationManager::class.java).cancel(alertId.toInt())
    }


    @SuppressLint("StringFormatInvalid")
    private fun buildTitle(context: Context, alert: WeatherAlert): String {
        val city = alert.cityName ?: context.getString(R.string.your_location)
        return context.getString(R.string.alert_notification_title, city)
    }

    private fun buildWeatherSummary(w: Weather): String =
        "${w.description.replaceFirstChar { it.uppercase() }} · " +
                "${w.temperature.toInt()}° · " +
                "💨 ${w.windSpeed} m/s · " +
                "☁ ${w.cloudiness}%"

    private fun buildDismissIntent(context: Context, alertId: Long): PendingIntent {
        val intent = Intent(context, AlertBroadcastReceiver::class.java).apply {
            action = AlertBroadcastReceiver.ACTION_END
            putExtra(AlertBroadcastReceiver.EXTRA_ALERT_ID, alertId)
        }
        return PendingIntent.getBroadcast(
            context,
            (alertId + 200_000L).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
private fun buildContentIntent(context: Context, alertId: Long): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("navigated_from_alert_id", alertId)
    }

    return PendingIntent.getActivity(
        context,
        alertId.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

