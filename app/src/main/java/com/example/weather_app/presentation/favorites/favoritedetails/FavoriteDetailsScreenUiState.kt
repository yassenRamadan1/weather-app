package com.example.weather_app.presentation.favorites.favoritedetails

import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.user.TemperatureUnit
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.domain.entity.user.WindSpeedUnit

import com.example.weather_app.presentation.uierror.UiText

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

    data class Error(val message: UiText) : FavoriteDetailsScreenUiState
}