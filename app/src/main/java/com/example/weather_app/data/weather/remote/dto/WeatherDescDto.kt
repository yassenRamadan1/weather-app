package com.example.weather_app.data.weather.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherDescDto(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)