package com.example.weather_app.di

import com.example.weather_app.domain.usecases.GetDailyForecastUseCase
import com.example.weather_app.domain.usecases.GetHourlyForecastUseCase
import com.example.weather_app.domain.usecases.GetPreferredLocationUseCase
import com.example.weather_app.domain.usecases.GetWeatherUseCase
import com.example.weather_app.domain.usecases.ObserveUserPreferencesUseCase
import com.example.weather_app.domain.usecases.UpdateLanguageUseCase
import com.example.weather_app.domain.usecases.UpdateLocationModeUseCase
import com.example.weather_app.domain.usecases.UpdateSavedLocationUseCase
import com.example.weather_app.domain.usecases.UpdateTemperatureUnitUseCase
import com.example.weather_app.domain.usecases.UpdateThemeUseCase
import com.example.weather_app.domain.usecases.UpdateWindSpeedUnitUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetWeatherUseCase(get()) }
    factory { GetHourlyForecastUseCase(get()) }
    factory { GetDailyForecastUseCase(get()) }
    factory {
        GetPreferredLocationUseCase(
            locationRepository = get(),
            userPreferencesDataSource = get()
        )
    }
    factory { UpdateSavedLocationUseCase(locationRepository = get()) }
    factory { ObserveUserPreferencesUseCase(userPreferencesRepository = get()) }
    factory { UpdateThemeUseCase(userPreferencesRepository = get()) }
    factory { UpdateLanguageUseCase(userPreferencesRepository = get()) }
    factory { UpdateTemperatureUnitUseCase(userPreferencesRepository = get()) }
    factory { UpdateWindSpeedUnitUseCase(userPreferencesRepository = get()) }
    factory { UpdateLocationModeUseCase(userPreferencesRepository = get()) }
}