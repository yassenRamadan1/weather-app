package com.example.weather_app.di

import com.example.weather_app.data.user.repository.UserPreferencesRepositoryImpl
import com.example.weather_app.data.weather.repository.WeatherRepositoryImpl
import com.example.weather_app.domain.repository.UserPreferencesRepository
import com.example.weather_app.domain.repository.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get(), get()) }
    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            locationProvider = get(),
            userPreferencesDataSource = get(),
            geocoder = get()
        )
    }
}