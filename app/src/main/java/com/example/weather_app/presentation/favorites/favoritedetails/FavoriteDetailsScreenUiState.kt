package com.example.weather_app.presentation.favorites.favoritedetails

import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.entity.HourlyWeather
import com.example.weather_app.domain.entity.LocationSource
import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.entity.Weather
import com.example.weather_app.domain.entity.WindSpeedUnit

sealed interface FavoriteDetailsScreenUiState{
    data object Loading : FavoriteDetailsScreenUiState

    data class Success(
        val currentWeather: Weather,
        val hourlyForecast: List<HourlyWeather>,
        val dailyForecast: List<DailyForecast>,
        val currentDateFormatted: String,
        val currentTimeFormatted: String,
        val temperatureUnit: TemperatureUnit,
        val windSpeedUnit: WindSpeedUnit,
        val isFromCache: Boolean = false,
    ) : FavoriteDetailsScreenUiState

    data class Error(val message: String) : FavoriteDetailsScreenUiState
}