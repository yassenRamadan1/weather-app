package com.example.weather_app.domain.usecases.alert

import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlertsUseCase(private val weatherRepository: WeatherRepository) {
    operator fun invoke(): Flow<List<WeatherAlert>> = weatherRepository.getAllAlerts()
}