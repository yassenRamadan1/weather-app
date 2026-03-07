package com.example.weather_app.data.weather.remote.datasource

import com.example.weather_app.data.weather.remote.dto.CurrentWeatherDto
import com.example.weather_app.data.weather.remote.service.WeatherApiService
import com.example.weather_app.domain.error.AppError
import retrofit2.HttpException
import java.io.IOException

class WeatherRemoteDataSourceImpl(
    private val weatherApiService: WeatherApiService,
    ) : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherDto> = runCatching {
        weatherApiService.getCurrentWeather(lat, lon,unit)
    }.mapError()



    private fun Result<CurrentWeatherDto>.mapError(): Result<CurrentWeatherDto> = this.fold(
        onSuccess = { Result.success(it) },
        onFailure = { throwable ->
            val appError = when (throwable) {
                is IOException -> AppError.NetworkError()
                is HttpException -> when (throwable.code()) {
                    401 -> AppError.UnauthorizedError()
                    404 -> AppError.NotFoundError()
                    in 500..599 -> AppError.ServerError(throwable.code(), throwable.message())
                    else -> AppError.UnknownError(throwable.message())
                }
                else -> AppError.UnknownError()
            }
            Result.failure(appError)
        })
}
