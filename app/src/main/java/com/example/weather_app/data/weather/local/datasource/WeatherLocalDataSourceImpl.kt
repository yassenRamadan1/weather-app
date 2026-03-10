package com.example.weather_app.data.weather.local.datasource

import com.example.weather_app.data.weather.local.ForecastDao
import com.example.weather_app.data.weather.local.WeatherDao
import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(
    private val weatherDao: WeatherDao,
    private val forecastDao: ForecastDao
) : WeatherLocalDataSource {

    override suspend fun saveWeatherData(weatherData: WeatherEntity) =
        weatherDao.insertWeather(weatherData)

    override suspend fun getWeatherData(lat: Double, lon: Double): Flow<WeatherEntity?> =
        weatherDao.getWeatherByCoordinates(lat, lon)

    override suspend fun replaceHourlyForecast(lat: Double, lon: Double, items: List<HourlyWeatherEntity>) =
        forecastDao.replaceHourlyWeather(lat, lon, items)

    override fun getHourlyForecast(lat: Double, lon: Double): Flow<List<HourlyWeatherEntity>> =
        forecastDao.getHourlyWeather(lat, lon)

    override suspend fun replaceDailyForecast(lat: Double, lon: Double, items: List<DailyForecastEntity>) =
        forecastDao.replaceDailyForecast(lat, lon, items)

    override fun getDailyForecast(lat: Double, lon: Double): Flow<List<DailyForecastEntity>> =
        forecastDao.getDailyForecast(lat, lon)
}
