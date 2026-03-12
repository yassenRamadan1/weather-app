package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.repository.WeatherRepository

class DeleteFavoriteLocationUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double) = repository.deleteFavoriteLocation(lat, lon)
}
