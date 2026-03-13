package com.example.weather_app.data.weather.remote.dto

import com.google.gson.annotations.SerializedName

data class CurrentWeatherDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("coord")
    val coord: CoordDto,
    @SerializedName("main")
    val main: MainDto,
    @SerializedName("weather")
    val weather: List<WeatherDescDto>,
    @SerializedName("wind")
    val wind: WindDto,
    @SerializedName("clouds")
    val clouds: CloudsDto,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("sys")
    val sys: SysDto
)
