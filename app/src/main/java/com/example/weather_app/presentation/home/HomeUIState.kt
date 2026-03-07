package com.example.weather_app.presentation.home

import com.example.weather_app.domain.entity.Weather

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val weather: Weather) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    data object LocationPermissionRequired : HomeUiState()
}