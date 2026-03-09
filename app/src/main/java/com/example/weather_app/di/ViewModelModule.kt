package com.example.weather_app.di

import com.example.weather_app.MainViewModel
import com.example.weather_app.presentation.home.HomeViewModel
import com.example.weather_app.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(),get()) }
    viewModel { SettingsViewModel(get(), get(),get(),get(),get(),get(),get(),get()) }
    viewModel { MainViewModel(get()) }
}