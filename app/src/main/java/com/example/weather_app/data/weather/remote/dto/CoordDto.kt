package com.example.weather_app.data.weather.remote.dto

import com.google.gson.annotations.SerializedName

data class CoordDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)
