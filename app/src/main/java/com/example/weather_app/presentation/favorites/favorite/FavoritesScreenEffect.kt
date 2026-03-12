package com.example.weather_app.presentation.favorites.favorite

sealed interface FavoritesScreenEffect {
        data class ShowMessage(val message: String) : FavoritesScreenEffect
}