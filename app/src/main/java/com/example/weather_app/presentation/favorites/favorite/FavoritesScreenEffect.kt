package com.example.weather_app.presentation.favorites.favorite

import com.example.weather_app.presentation.uierror.UiText

sealed interface FavoritesScreenEffect {
        data class ShowMessage(val message: UiText) : FavoritesScreenEffect
}