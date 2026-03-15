package com.example.weather_app.domain.entity.weather

data class HourlyWeather(
    val timestamp: Long,
    val temperature: Double,
    val weatherStateId: Int,
    val iconCode: String,
    val description: String,
    val windSpeed: Double,
    val humidity: Int,
    val pop: Double
) {
    val weatherState: Weather.WeatherState get() = weatherStateId.toWeatherState()
    val isDay: Boolean get() = iconCode.endsWith("d")
}
