package com.example.weather_app.presentation.favorites.favoritedetails

import com.example.weather_app.domain.entity.user.UserPreferences
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.weather.Weather
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class FavoriteDetailsUiMapper {
    fun mapToSuccess(
        weather: Weather,
        hourly: List<HourlyWeather>,
        daily: List<DailyForecast>,
        prefs: UserPreferences,
        isFromCache: Boolean
    ): FavoriteDetailsScreenUiState.Success {
        val now = Instant.now().atZone(ZoneId.systemDefault())
        val locale = Locale(prefs.language.code)
        
        val dateFormatted = now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
        val timeFormatted = now.format(DateTimeFormatter.ofPattern("HH:mm", locale))

        return FavoriteDetailsScreenUiState.Success(
            currentWeather = weather,
            hourlyForecast = hourly,
            dailyForecast = daily,
            currentDateFormatted = dateFormatted,
            currentTimeFormatted = timeFormatted,
            temperatureUnit = prefs.temperatureUnit,
            windSpeedUnit = prefs.windSpeedUnit,
            isFromCache = isFromCache
        )
    }
}
