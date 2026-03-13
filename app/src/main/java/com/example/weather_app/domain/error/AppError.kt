package com.example.weather_app.domain.error

sealed class AppError : Exception() {
    data class NetworkError(override val message: String? = null) : AppError()
    data class ServerError(val code: Int, override val message: String? = null) : AppError()
    data class UnauthorizedError(override val message: String? = null) : AppError()
    data class NotFoundError(override val message: String? = null) : AppError()
    data class UnknownError(override val message: String? = null) : AppError()
    data class CacheError(override val message: String? = null) : AppError()
    data class ValidationError(override val message: String) : AppError()

    object NoDaysSelected : AppError()
    object InvalidPeriod : AppError()
    object PeriodOrder : AppError()
    data class ThresholdNeg(val threshold: Double) : AppError()
}
