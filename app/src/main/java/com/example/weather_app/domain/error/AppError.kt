package com.example.weather_app.domain.error

sealed class AppError : Exception() {
    data class NetworkError(override val message: String = "No internet connection") : AppError()
    data class ServerError(val code: Int, override val message: String) : AppError()
    data class UnauthorizedError(override val message: String = "Invalid API Key") : AppError()
    data class NotFoundError(override val message: String = "Location not found") : AppError()
    data class UnknownError(override val message: String = "An unexpected error occurred") : AppError()
    data class CacheError(override val message: String = "Failed to access local data") : AppError()
}
fun AppError.toUiMessage(): String = this.message ?: "Unknown error"