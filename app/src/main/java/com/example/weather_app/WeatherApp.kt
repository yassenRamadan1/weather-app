package com.example.weather_app

import android.app.Application
import androidx.work.Configuration
import com.example.weather_app.data.weather.notification.AlertNotificationManager
import com.example.weather_app.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApp : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        AlertNotificationManager.createChannels(this)

        startKoin {
            androidContext(this@WeatherApp)
            modules(
                databaseModule,
                dataSourceModule,
                networkModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                locationModule,
                schedulerModule
            )
        }
    }
}