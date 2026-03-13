package com.example.weather_app.presentation.uierror

import com.example.weather_app.R
import com.example.weather_app.domain.error.AppError

fun AppError.toUiText(): UiText {
    return when (this) {
        is AppError.CacheError -> UiText.StringResource(R.string.error_cache)
        AppError.InvalidPeriod -> UiText.StringResource(R.string.error_invalid_period)
        is AppError.NetworkError -> UiText.StringResource(R.string.error_network)
        AppError.NoDaysSelected -> UiText.StringResource(R.string.error_no_days_selected)
        is AppError.NotFoundError -> UiText.StringResource(R.string.error_not_found)
        AppError.PeriodOrder -> UiText.StringResource(R.string.error_period_order)
        is AppError.ServerError -> UiText.StringResource(R.string.error_server, code)
        is AppError.ThresholdNeg -> UiText.StringResource(R.string.error_threshold_neg, threshold)
        is AppError.UnauthorizedError -> UiText.StringResource(R.string.error_unauthorized)
        is AppError.UnknownError -> UiText.StringResource(R.string.error_unknown)
        is AppError.ValidationError -> UiText.StringResource(R.string.error_validation, message)
    }
}
