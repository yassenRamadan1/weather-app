package com.example.weather_app.data.weather.repository

import com.example.weather_app.data.weather.local.datasource.WeatherLocalDataSource
import com.example.weather_app.data.weather.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FakeWeatherLocalDataSource : WeatherLocalDataSource {
    private val weatherData = mutableMapOf<Pair<Double, Double>, WeatherEntity>()
    private val hourlyForecasts = mutableMapOf<Pair<Double, Double>, List<HourlyWeatherEntity>>()
    private val dailyForecasts = mutableMapOf<Pair<Double, Double>, List<DailyForecastEntity>>()
    private val favorites = mutableListOf<FavoriteLocationEntity>()
    private val alerts = mutableListOf<WeatherAlertEntity>()

    override suspend fun saveWeatherData(weatherData: WeatherEntity) {
        this.weatherData[Pair(weatherData.lat, weatherData.lon)] = weatherData
    }

    override suspend fun getWeatherData(lat: Double, lon: Double): Flow<WeatherEntity?> = flow {
        emit(weatherData.values.find { Math.abs(it.lat - lat) < 0.1 && Math.abs(it.lon - lon) < 0.1 })
    }

    override suspend fun replaceHourlyForecast(lat: Double, lon: Double, items: List<HourlyWeatherEntity>) {
        hourlyForecasts[Pair(lat, lon)] = items
    }

    override fun getHourlyForecast(lat: Double, lon: Double): Flow<List<HourlyWeatherEntity>> = flow {
        emit(hourlyForecasts[Pair(lat, lon)] ?: emptyList())
    }

    override suspend fun replaceDailyForecast(lat: Double, lon: Double, items: List<DailyForecastEntity>) {
        dailyForecasts[Pair(lat, lon)] = items
    }

    override fun getDailyForecast(lat: Double, lon: Double): Flow<List<DailyForecastEntity>> = flow {
        emit(dailyForecasts[Pair(lat, lon)] ?: emptyList())
    }

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocationEntity>> = flow {
        emit(favorites.toList())
    }

    override suspend fun deleteFavoriteLocation(lat: Double, lon: Double) {
        favorites.removeAll { it.lat == lat && it.lon == lon }
    }

    override suspend fun addFavoriteLocation(favoriteLocationEntity: FavoriteLocationEntity) {
        favorites.add(favoriteLocationEntity)
    }

    override suspend fun deleteWeatherData(lat: Double, lon: Double) {
        weatherData.remove(Pair(lat, lon))
        hourlyForecasts.remove(Pair(lat, lon))
        dailyForecasts.remove(Pair(lat, lon))
    }

    override fun getAllAlerts(): Flow<List<WeatherAlertEntity>> = flow {
        emit(alerts.toList())
    }

    override suspend fun getAlertById(id: Long): WeatherAlertEntity? = alerts.find { it.id == id }

    override suspend fun insertAlert(entity: WeatherAlertEntity): Long {
        val id = (alerts.size + 1).toLong()
        alerts.add(entity.copy(id = id))
        return id
    }

    override suspend fun deleteAlert(id: Long) {
        alerts.removeAll { it.id == id }
    }

    override suspend fun updateActive(id: Long, isActive: Boolean) {
        val index = alerts.indexOfFirst { it.id == id }
        if (index != -1) {
            alerts[index] = alerts[index].copy(isActive = isActive)
        }
    }

    override suspend fun getActiveAlerts(): List<WeatherAlertEntity> = alerts.filter { it.isActive }
}
