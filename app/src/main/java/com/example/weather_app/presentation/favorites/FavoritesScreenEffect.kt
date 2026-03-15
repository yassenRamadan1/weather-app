package com.example.weather_app.presentation.favorites

import com.example.weather_app.presentation.uierror.UiText

sealed interface FavoritesScreenEffect {
        data class ShowMessage(val message: UiText) : FavoritesScreenEffect
}