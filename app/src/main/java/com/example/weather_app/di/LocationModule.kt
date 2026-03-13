package com.example.weather_app.di

import android.location.Geocoder
import com.example.weather_app.data.location.AndroidLocationProvider
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.Locale

import com.example.weather_app.domain.repository.LocationProvider

val locationModule = module {

    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    single<LocationProvider> { AndroidLocationProvider(context = androidContext(), fusedClient = get()) }

    single { Geocoder(androidContext(), Locale.getDefault()) }

    }