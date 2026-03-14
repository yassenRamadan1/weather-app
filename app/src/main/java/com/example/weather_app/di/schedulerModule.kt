package com.example.weather_app.di

import com.example.weather_app.data.services.AlarmSchedulerImpl
import com.example.weather_app.domain.service.AlarmScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val schedulerModule = module {
    single<AlarmScheduler> { AlarmSchedulerImpl(androidContext()) }
}