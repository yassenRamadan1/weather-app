package com.example.weather_app.presentation.alerts.components.bottomsheet

import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.presentation.uierror.UiText

data class AddAlertFormState(
    val startTimeMillis: Long? = null,
    val endTimeMillis: Long? = null,
    val alertType: AlertType = AlertType.NOTIFICATION,
    val conditionMode: AlertConditionMode = AlertConditionMode.ANY,
    val temperatureThreshold: String = "",
    val windThreshold: String = "",
    val cloudinessThreshold: String = "",
    val isRepeated: Boolean = false,
    val startError: UiText? = null,
    val endError: UiText? = null,
    val conditionError: UiText? = null,
    val isSaving: Boolean = false
) {
    val canSave: Boolean
        get() = startTimeMillis != null
                && endTimeMillis != null
                && startError == null
                && endError == null
                && !isSaving
}