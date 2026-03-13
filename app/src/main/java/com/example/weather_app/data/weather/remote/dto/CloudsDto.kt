package com.example.weather_app.data.weather.remote.dto

import com.google.gson.annotations.SerializedName

data class CloudsDto(
    @SerializedName("all") val all: Int
)