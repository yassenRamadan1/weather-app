package com.example.weather_app.presentation.favorites.favoritedetails

sealed interface FavoriteDetailsEffect {
        data class ShowError(val message: Int) : FavoriteDetailsEffect
}