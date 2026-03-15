package com.example.weather_app.domain.usecases

import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.entity.weather.*
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeWeatherRepository : WeatherRepository {
    var currentWeather: Result<Weather>? = null
    var hourlyForecast: Result<List<HourlyWeather>>? = null
    var dailyForecast: Result<List<DailyForecast>>? = null
    
    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favoritesList: List<FavoriteLocation> get() = _favorites.value

    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alertsList: List<WeatherAlert> get() = _alerts.value

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<Result<Weather>> = flow {
        currentWeather?.let { emit(it) }
    }

    override fun getHourlyForecast(lat: Double, lon: Double): Flow<Result<List<HourlyWeather>>> = flow {
        hourlyForecast?.let { emit(it) }
    }

    override fun getDailyForecast(lat: Double, lon: Double): Flow<Result<List<DailyForecast>>> = flow {
        dailyForecast?.let { emit(it) }
    }

    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> = _favorites

    override suspend fun addFavoriteLocation(location: FavoriteLocation) {
        _favorites.update { it + location }
    }

    override suspend fun deleteFavoriteLocation(lat: Double, lon: Double) {
        _favorites.update { list -> list.filterNot { it.lat == lat && it.lon == lon } }
    }

    override fun getAllAlerts(): Flow<List<WeatherAlert>> = _alerts

    override suspend fun addAlert(alert: WeatherAlert): Long {
        val id = (_alerts.value.size + 1).toLong()
        val newAlert = alert.copy(id = id)
        _alerts.update { it + newAlert }
        return id
    }

    override suspend fun getAlertById(id: Long): WeatherAlert? = _alerts.value.find { it.id == id }

    override suspend fun deleteAlert(id: Long) {
        _alerts.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun setAlertActive(id: Long, isActive: Boolean) {
        _alerts.update { list ->
            list.map { if (it.id == id) it.copy(isActive = isActive) else it }
        }
    }

    override suspend fun getActiveAlerts(): List<WeatherAlert> = _alerts.value.filter { it.isActive }
}
