package com.example.weather_app.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.weather_app.data.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "weather_db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().weatherDao() }

    single { androidContext().dataStore }
}