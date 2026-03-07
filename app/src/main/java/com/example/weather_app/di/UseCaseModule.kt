package com.example.weather_app.di

import com.example.weather_app.domain.usecase.GetWeatherUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetWeatherUseCase(get()) }
}