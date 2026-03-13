package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.weather.FavoriteLocation
import com.example.weather_app.domain.repository.WeatherRepository

class AddFavoriteLocationUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(location: FavoriteLocation) = repository.addFavoriteLocation(location)
}
