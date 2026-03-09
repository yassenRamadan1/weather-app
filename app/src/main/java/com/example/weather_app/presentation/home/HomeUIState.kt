package com.example.weather_app.presentation.home

import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.entity.HourlyWeather
import com.example.weather_app.domain.entity.LocationSource
import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.entity.Weather
import com.example.weather_app.domain.entity.WindSpeedUnit

sealed class HomeUiState {
    data object Loading : HomeUiState()

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
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
    data object NeedLocationPermission : HomeUiState()
    data object GpsDisabled : HomeUiState()
    data object GpsNoFix : HomeUiState()
    data object NeedManualLocation : HomeUiState()
}
