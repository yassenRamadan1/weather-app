package com.example.weather_app.data.user.repository

import android.location.Location
import com.example.weather_app.domain.repository.LocationProvider

class FakeLocationProvider : LocationProvider {
    var hasPermission: Boolean = true
    var isEnabled: Boolean = true
    var currentLocation: Location? = null
    var lastLocation: Location? = null

    override fun hasPermission(): Boolean = hasPermission
    override fun isLocationServicesEnabled(): Boolean = isEnabled
    override suspend fun getCurrentLocationOrLastKnown(): Location? = currentLocation
    override suspend fun tryGetLastLocation(): Location? = lastLocation
}
