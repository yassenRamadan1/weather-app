package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetHourlyForecastUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke(lat: Double, lon: Double): Flow<Result<List<HourlyWeather>>> =
        repository.getHourlyForecast(lat, lon)
}
