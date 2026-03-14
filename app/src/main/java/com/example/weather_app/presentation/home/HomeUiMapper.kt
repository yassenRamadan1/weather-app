package com.example.weather_app.presentation.home

import com.example.weather_app.domain.entity.user.LocationSource
import com.example.weather_app.domain.entity.user.UserPreferences
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.weather.Weather
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeUiMapper {
    fun mapToSuccess(
        weather: Weather,
        hourly: List<HourlyWeather>,
        daily: List<DailyForecast>,
        prefs: UserPreferences,
        isStale: Boolean,
        source: LocationSource,
        isFromCache: Boolean
    ): HomeUiState.Success {
        val now = Instant.now().atZone(ZoneId.systemDefault())
        val locale = Locale(prefs.language.code)
        
        val dateFormatted = now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
        val timeFormatted = now.format(DateTimeFormatter.ofPattern("HH:mm", locale))

        return HomeUiState.Success(
            currentWeather = weather,
            hourlyForecast = hourly,
            dailyForecast = daily,
            isStaleLocation = isStale,
            locationSource = source,
            currentDateFormatted = dateFormatted,
            currentTimeFormatted = timeFormatted,
            temperatureUnit = prefs.temperatureUnit,
            windSpeedUnit = prefs.windSpeedUnit,
            isFromCache = isFromCache
        )
    }
}
