package com.example.weather_app.data.weather.remote.dto

import com.google.gson.annotations.SerializedName

data class SysDto(
    @SerializedName("country") val country: String
)