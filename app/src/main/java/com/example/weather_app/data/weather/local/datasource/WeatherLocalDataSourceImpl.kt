package com.example.weather_app.data.weather.local.datasource

import com.example.weather_app.data.weather.local.WeatherDao
import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(
private val weatherDao: WeatherDao
) : WeatherLocalDataSource {
    override suspend fun saveWeatherData(weatherData: WeatherEntity) =
        weatherDao.insertWeather(weatherData)


    override suspend fun getWeatherData(
        lat: Double,
        lon: Double
    ): Flow<WeatherEntity?> =
    weatherDao.getWeatherByCoordinates(lat, lon )

}