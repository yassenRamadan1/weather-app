package com.example.weather_app.data.weather.local.datasource

import com.example.weather_app.data.weather.local.dao.FavoriteLocationDao
import com.example.weather_app.data.weather.local.dao.ForecastDao
import com.example.weather_app.data.weather.local.dao.WeatherDao
import com.example.weather_app.data.weather.local.entity.DailyForecastEntity
import com.example.weather_app.data.weather.local.entity.FavoriteLocationEntity
import com.example.weather_app.data.weather.local.entity.HourlyWeatherEntity
import com.example.weather_app.data.weather.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(
    private val weatherDao: WeatherDao,
    private val forecastDao: ForecastDao,
    private val favoriteLocationDao: FavoriteLocationDao
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

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>> =
        favoriteLocationDao.getFavoriteLocations()


    override suspend fun deleteFavoriteLocation(lat: Double, lon: Double) {
        favoriteLocationDao.removeFavorite(lat,lon)
    }

    override suspend fun addFavoriteLocation(favoriteLocationEntity: FavoriteLocationEntity) {
        favoriteLocationDao.addFavorite(favoriteLocationEntity)
    }

    override suspend fun deleteWeatherData(lat: Double, lon: Double) {
        weatherDao.deleteWeatherByCoordinates(lat,lon)
        forecastDao.deleteHourlyWeather(lat,lon)
        forecastDao.deleteDailyForecast(lat,lon)
    }

}
