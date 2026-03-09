package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetDailyForecastUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke(lat: Double, lon: Double): Flow<Result<List<DailyForecast>>> =
        repository.getDailyForecast(lat, lon)
}
