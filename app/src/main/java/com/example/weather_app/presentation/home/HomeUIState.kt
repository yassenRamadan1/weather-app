package com.example.weather_app.presentation.home

import com.example.weather_app.domain.entity.LocationSource
import com.example.weather_app.domain.entity.Weather

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val weather: Weather,
        val isStaleLocation: Boolean = false,
        val locationSource: LocationSource = LocationSource.GPS
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
    data object NeedLocationPermission : HomeUiState()

    data object GpsDisabled : HomeUiState()
    data object NeedManualLocation : HomeUiState()
}