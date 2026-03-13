package com.example.weather_app.data.weather.repository

import com.example.weather_app.data.weather.filterHourlyForToday
import com.example.weather_app.data.weather.local.datasource.WeatherLocalDataSource
import com.example.weather_app.data.weather.remote.datasource.WeatherRemoteDataSource
import com.example.weather_app.data.weather.toDailyForecasts
import com.example.weather_app.data.weather.toDomain
import com.example.weather_app.data.weather.toEntity
import com.example.weather_app.data.user.local.UserPreferencesDataSource
import com.example.weather_app.data.weather.toDomainList
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.weather.FavoriteLocation
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class WeatherRepositoryImpl(
    private val remote: WeatherRemoteDataSource,
    private val local: WeatherLocalDataSource,
    private val userPrefs: UserPreferencesDataSource
) : WeatherRepository {

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<Result<Weather>> = flow {
        val cached = local.getWeatherData(lat = lat, lon = lon).first()
        if (cached != null) emit(Result.success(cached.toDomain()))
        val prefs = userPrefs.userPreferences.first()
        val result =
            remote.getCurrentWeather(lat, lon, prefs.temperatureUnit.apiValue, prefs.language.code)
        result.onSuccess { weather ->
            local.saveWeatherData(weather.toEntity().copy(lat = lat, lon = lon))
            emit(Result.success(weather.toDomain()))
        }.onFailure { error ->
            emit(Result.failure(error))
        }
    }.flowOn(Dispatchers.IO)

    override fun getHourlyForecast(lat: Double, lon: Double): Flow<Result<List<HourlyWeather>>> =
        flow {
            val cached = local.getHourlyForecast(lat, lon).first()
            if (cached.isNotEmpty()) emit(Result.success(cached.map { it.toDomain() }))
            val prefs = userPrefs.userPreferences.first()
            val result =
                remote.getForecast(lat, lon, prefs.temperatureUnit.apiValue, prefs.language.code)
            result.onSuccess { response ->
                val hourlyList = response.filterHourlyForToday()
                local.replaceHourlyForecast(lat, lon, hourlyList.map { it.toEntity(lat, lon) })
                emit(Result.success(hourlyList))
            }.onFailure { error ->
                emit(Result.failure(error))
            }
        }.flowOn(Dispatchers.IO)

    override fun getDailyForecast(lat: Double, lon: Double): Flow<Result<List<DailyForecast>>> =
        flow {
            val cached = local.getDailyForecast(lat, lon).first()
            if (cached.isNotEmpty()) emit(Result.success(cached.map { it.toDomain() }))
            val prefs = userPrefs.userPreferences.first()
            val result =
                remote.getForecast(lat, lon, prefs.temperatureUnit.apiValue, prefs.language.code)
            result.onSuccess { response ->
                val dailyList = response.toDailyForecasts()
                local.replaceDailyForecast(lat, lon, dailyList.map { it.toEntity(lat, lon) })
                emit(Result.success(dailyList))
            }.onFailure { error ->
                emit(Result.failure(error))
            }
        }.flowOn(Dispatchers.IO)

    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> =
        local.getAllFavoriteLocations().map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)

    override suspend fun addFavoriteLocation(location: FavoriteLocation) {
        local.addFavoriteLocation(location.toEntity())
    }

    override suspend fun deleteFavoriteLocation(lat: Double, lon: Double) {
        local.deleteFavoriteLocation(lat, lon)
    }

    override fun getAllAlerts(): Flow<List<WeatherAlert>> =
        local.getAllAlerts().map { list ->
            list.toDomainList()
        }.flowOn(Dispatchers.IO)

    override suspend fun addAlert(alert: WeatherAlert): Long = local.insertAlert(alert.toEntity())
    override suspend fun deleteAlert(id: Long) {
        local.deleteAlert(id)
    }
    override suspend fun setAlertActive(id: Long, isActive: Boolean) {
        local.updateActive(id, isActive)
    }
    override suspend fun getActiveAlerts(): List<WeatherAlert> = local.getActiveAlerts().toDomainList()

}
