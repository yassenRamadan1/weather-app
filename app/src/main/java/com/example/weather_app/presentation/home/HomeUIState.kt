package com.example.weather_app.presentation.home

import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.user.LocationSource
import com.example.weather_app.domain.entity.user.TemperatureUnit
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.domain.entity.user.WindSpeedUnit

import com.example.weather_app.presentation.uierror.UiText

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val currentWeather: Weather,
        val hourlyForecast: List<HourlyWeather>,
        val dailyForecast: List<DailyForecast>,
        val isStaleLocation: Boolean = false,
        val locationSource: LocationSource = LocationSource.GPS,
        val currentDateFormatted: String,
        val currentTimeFormatted: String,
        val temperatureUnit: TemperatureUnit,
        val windSpeedUnit: WindSpeedUnit,
        val isFromCache: Boolean = false,
    ) : HomeUiState

    data class Error(val message: UiText) : HomeUiState
    data object NeedLocationPermission : HomeUiState
    data object GpsDisabled : HomeUiState
    data object GpsNoFix : HomeUiState
    data object NeedManualLocation : HomeUiState
}