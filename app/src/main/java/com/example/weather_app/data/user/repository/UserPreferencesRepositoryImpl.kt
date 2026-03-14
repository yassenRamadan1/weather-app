package com.example.weather_app.data.user.repository

import com.example.weather_app.domain.repository.LocationProvider
import com.example.weather_app.data.user.local.UserPreferencesDataSource
import com.example.weather_app.domain.entity.user.AppLanguage
import com.example.weather_app.domain.entity.user.AppTheme
import com.example.weather_app.domain.entity.user.LocationMode
import com.example.weather_app.domain.entity.user.LocationResult
import com.example.weather_app.domain.entity.user.LocationSource
import com.example.weather_app.domain.entity.user.TemperatureUnit
import com.example.weather_app.domain.entity.user.UserPreferences
import com.example.weather_app.domain.entity.user.WindSpeedUnit
import com.example.weather_app.domain.repository.UserPreferencesRepository
import com.example.weather_app.domain.repository.GeocodingProvider
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl(
    private val locationProvider: LocationProvider,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val geocodingProvider: GeocodingProvider
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = userPreferencesDataSource.userPreferences

    override suspend fun updateTheme(theme: AppTheme) =
        userPreferencesDataSource.updateTheme(theme)

    override suspend fun updateLanguage(language: AppLanguage) =
        userPreferencesDataSource.updateLanguage(language)

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) =
        userPreferencesDataSource.updateTemperatureUnit(unit)

    override suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) =
        userPreferencesDataSource.updateWindSpeedUnit(unit)

    override suspend fun updateLocationMode(mode: LocationMode) =
        userPreferencesDataSource.updateLocationMode(mode)

    override suspend fun getPreferredLocation(savedPreferences: UserPreferences): LocationResult {
        return when (savedPreferences.locationMode) {
            LocationMode.MAP -> resolveMapLocation(savedPreferences)
            LocationMode.GPS -> resolveGpsLocation(savedPreferences)
        }
    }
    private suspend fun resolveMapLocation(prefs: UserPreferences): LocationResult {
        val lat = prefs.savedLat
        val lon = prefs.savedLon
        return if (lat != null && lon != null) {
            val city = geocodingProvider.reverseGeocode(lat, lon)
            LocationResult.Success(lat, lon, city, LocationSource.SAVED_PREFERENCES, isStale = false)
        } else {
            LocationResult.NoSavedLocation
        }
    }

    private suspend fun resolveGpsLocation(prefs: UserPreferences): LocationResult {
        if (!locationProvider.hasPermission()) {
            return LocationResult.NeedPermission
        }

        val gpsEnabled = locationProvider.isLocationServicesEnabled()
        if (gpsEnabled) {
            val deviceLocation = locationProvider.getCurrentLocationOrLastKnown()
            if (deviceLocation != null) {
                val city = geocodingProvider.reverseGeocode(deviceLocation.latitude, deviceLocation.longitude)
                userPreferencesDataSource.updateSavedLocation(
                    deviceLocation.latitude, deviceLocation.longitude
                )
                return LocationResult.Success(
                    lat = deviceLocation.latitude,
                    lon = deviceLocation.longitude,
                    cityName = city,
                    source = LocationSource.GPS,
                    isStale = false
                )
            }
        }

        val lastKnown = locationProvider.tryGetLastLocation()
        if (lastKnown != null) {
            val city = geocodingProvider.reverseGeocode(lastKnown.latitude, lastKnown.longitude)
            userPreferencesDataSource.updateSavedLocation(lastKnown.latitude, lastKnown.longitude)
            return LocationResult.Success(
                lat = lastKnown.latitude,
                lon = lastKnown.longitude,
                cityName = city,
                source = LocationSource.LAST_KNOWN,
                isStale = true
            )
        }
        val savedLat = prefs.savedLat
        val savedLon = prefs.savedLon
        if (savedLat != null && savedLon != null) {
            val city = geocodingProvider.reverseGeocode(savedLat, savedLon)
            return LocationResult.Success(
                lat = savedLat,
                lon = savedLon,
                cityName = city,
                source = LocationSource.SAVED_PREFERENCES,
                isStale = true
            )
        }
        return if (!gpsEnabled) LocationResult.GpsDisabled else LocationResult.GpsNoFix
    }

    override suspend fun updateSavedLocation(lat: Double, lon: Double) {
        userPreferencesDataSource.updateSavedLocation(lat, lon)
    }
}