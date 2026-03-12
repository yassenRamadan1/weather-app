package com.example.weather_app.presentation.favorites.favorite

import com.example.weather_app.domain.entity.FavoriteLocation

sealed interface FavoritesScreenUiState {
    data class Success(
        val favoriteLocations: List<FavoriteLocation>
    ) : FavoritesScreenUiState

    data object Loading : FavoritesScreenUiState

    data class Error(val message: String) : FavoritesScreenUiState
    data object Empty : FavoritesScreenUiState
}