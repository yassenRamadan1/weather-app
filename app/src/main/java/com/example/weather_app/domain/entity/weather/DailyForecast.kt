package com.example.weather_app.domain.entity.weather

data class DailyForecast(
    val timestamp: Long,
    val minTemp: Double,
    val maxTemp: Double,
    val weatherStateId: Int,
    val iconCode: String,
    val description: String,
    val humidity: Int,
    val windSpeed: Double
) {
    val weatherState: Weather.WeatherState get() = weatherStateId.toWeatherState()
    val isDay: Boolean get() = iconCode.endsWith("d")
}
