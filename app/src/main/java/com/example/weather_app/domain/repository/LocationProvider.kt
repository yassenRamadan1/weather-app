package com.example.weather_app.domain.repository

import android.location.Location

interface LocationProvider {
    fun hasPermission(): Boolean
    fun isLocationServicesEnabled(): Boolean
    suspend fun getCurrentLocationOrLastKnown(): Location?
    suspend fun tryGetLastLocation(): Location?
}
