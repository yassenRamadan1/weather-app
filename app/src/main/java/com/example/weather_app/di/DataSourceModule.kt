package com.example.weather_app.di

import com.example.weather_app.data.user.local.UserPreferencesDataSourceImpl
import com.example.weather_app.data.weather.local.datasource.WeatherLocalDataSource
import com.example.weather_app.data.weather.local.datasource.WeatherLocalDataSourceImpl
import com.example.weather_app.data.weather.remote.datasource.WeatherRemoteDataSource
import com.example.weather_app.data.weather.remote.datasource.WeatherRemoteDataSourceImpl
import com.example.weather_app.domain.datasource.UserPreferencesDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single<WeatherRemoteDataSource> { WeatherRemoteDataSourceImpl(get()) }
    single<WeatherLocalDataSource> { WeatherLocalDataSourceImpl(get(), get()) }
    single<UserPreferencesDataSource> { UserPreferencesDataSourceImpl(get()) }
}
