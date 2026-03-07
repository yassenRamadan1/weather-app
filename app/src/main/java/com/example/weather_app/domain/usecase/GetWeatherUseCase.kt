package com.example.weather_app.domain.usecase

import com.example.weather_app.domain.repository.WeatherRepository

class GetWeatherUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke(lat: Double, lon: Double) = repository.getCurrentWeather(lat, lon)
}