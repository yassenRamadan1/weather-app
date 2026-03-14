package com.example.weather_app.di

import com.example.weather_app.data.location.AndroidGeocodingProvider
import com.example.weather_app.domain.repository.GeocodingProvider
import com.example.weather_app.data.location.AndroidLocationProvider
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.example.weather_app.domain.repository.LocationProvider

val locationModule = module {

    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    single<LocationProvider> { AndroidLocationProvider(context = androidContext(), fusedClient = get()) }

    single { android.location.Geocoder(androidContext()) }
    
    single<GeocodingProvider> { AndroidGeocodingProvider(get()) }
}