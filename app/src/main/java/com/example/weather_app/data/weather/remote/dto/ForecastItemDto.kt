package com.example.weather_app.data.weather.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherDescDto>,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("clouds") val clouds: CloudsDto,
    @SerializedName("pop") val pop: Double,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("dt_txt") val dtTxt: String
)
