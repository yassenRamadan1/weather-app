package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.repository.WeatherRepository

class GetFavoriteLocationsUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.getFavoriteLocations()
}
