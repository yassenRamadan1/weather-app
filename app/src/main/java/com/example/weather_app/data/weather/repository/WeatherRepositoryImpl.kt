package com.example.weather_app.data.weather.repository

import com.example.weather_app.data.weather.local.datasource.WeatherLocalDataSource
import com.example.weather_app.data.weather.remote.datasource.WeatherRemoteDataSource
import com.example.weather_app.data.weather.toDomain
import com.example.weather_app.data.weather.toEntity
import com.example.weather_app.domain.datasource.UserPreferencesDataSource
import com.example.weather_app.domain.entity.Weather
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRepositoryImpl(
    private val remote: WeatherRemoteDataSource,
    private val local: WeatherLocalDataSource,
    private val userPrefs: UserPreferencesDataSource
): WeatherRepository {

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<Result<Weather>> = flow {
        val cached = local.getWeatherData(lat=lat,lon = lon).first()
        if (cached != null) emit(Result.success(cached.toDomain()))
        val prefs = userPrefs.userPreferences.first()
        val units = prefs.temperatureUnit.apiValue
        val lang = prefs.language.code
        val result = remote.getCurrentWeather(lat, lon, units, lang)
        result.onSuccess { weather ->
            local.saveWeatherData(weather.toEntity())
            emit(Result.success(weather.toDomain()))
        }.onFailure { error ->
            if (cached == null) emit(Result.failure(error))
        }
    }.flowOn(Dispatchers.IO)

}