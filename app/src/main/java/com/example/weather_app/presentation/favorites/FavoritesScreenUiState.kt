package com.example.weather_app.presentation.favorites

import com.example.weather_app.domain.entity.weather.FavoriteLocation

import com.example.weather_app.presentation.uierror.UiText

sealed interface FavoritesScreenUiState {
    data class Success(
        val favoriteLocations: List<FavoriteLocation>
    ) : FavoritesScreenUiState

    data object Loading : FavoritesScreenUiState

    data class Error(val message: UiText) : FavoritesScreenUiState
    data object Empty : FavoritesScreenUiState
}