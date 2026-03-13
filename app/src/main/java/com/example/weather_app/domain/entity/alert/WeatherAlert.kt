package com.example.weather_app.domain.entity.alert

data class WeatherAlert(
    val id: Long = 0,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val alertType: AlertType,
    val conditionMode: AlertConditionMode,
    val temperatureThreshold: Double? = null,
    val windThreshold: Double? = null,
    val cloudinessThreshold: Int? = null,
    val isActive: Boolean = true,
    val lat: Double,
    val lon: Double,
    val cityName: String? = null
)
enum class AlertConditionMode {
    ANY,
    CONDITIONS
}
enum class AlertType {
    ALARM,
    NOTIFICATION
}