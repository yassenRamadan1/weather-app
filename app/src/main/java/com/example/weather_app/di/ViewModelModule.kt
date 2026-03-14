package com.example.weather_app.di

import com.example.weather_app.main.MainViewModel
import com.example.weather_app.presentation.alerts.AlertsScreenViewModel
import com.example.weather_app.presentation.favorites.FavoriteScreenViewModel
import com.example.weather_app.presentation.favorites.favoritedetails.FavoriteDetailsViewModel
import com.example.weather_app.presentation.home.HomeViewModel
import com.example.weather_app.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { MainViewModel(get()) }
    viewModel { FavoriteScreenViewModel(get(), get(), get()) }
    viewModel { FavoriteDetailsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel {
        AlertsScreenViewModel(
            getAllAlerts = get(),
            addAlertUseCase = get(),
            deleteAlertUseCase = get(),
            setAlertActiveUseCase = get(),
            getPreferredLocationUseCase = get()
        )
    }
}