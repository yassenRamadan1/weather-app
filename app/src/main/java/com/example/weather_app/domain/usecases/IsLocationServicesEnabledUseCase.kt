package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.repository.LocationProvider

class IsLocationServicesEnabledUseCase(
    private val locationProvider: LocationProvider
) {
    operator fun invoke(): Boolean {
        return locationProvider.isLocationServicesEnabled()
    }
}
