package com.example.weather_app.domain.entity.weather

data class Weather(
    val cityName: String,
    val weatherStateId: Int,
    val countryCode: String,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val cloudiness: Int,
    val visibility: Int,
    val timestamp: Long,
    val lat: Double,
    val lon: Double
) {
    enum class WeatherState {
        ClearSky,
        MainlyClear,
        PartlyCloudy,
        Overcast,
        Fog,
        DepositingRimeFog,
        DrizzleLight,
        DrizzleModerate,
        DrizzleDense,
        FreezingDrizzleLight,
        FreezingDrizzleDense,
        RainSlight,
        RainModerate,
        RainHeavy,
        FreezingRainLight,
        FreezingRainHeavy,
        SnowSlight,
        SnowModerate,
        SnowHeavy,
        SnowGrains,
        RainShowerSlight,
        RainShowerModerate,
        RainShowerViolent,
        SnowShowerSlight,
        SnowShowerHeavy,
        Thunderstorm,
        ThunderstormWithSlightHail,
        ThunderstormWithHeavyHail,
        Unknown
    }

    val weatherState: WeatherState get() = weatherStateId.toWeatherState()
    val isDay: Boolean get() = iconCode.endsWith("d")
}

fun Int.toWeatherState(): Weather.WeatherState {
    return when (this) {
        800 -> Weather.WeatherState.ClearSky
        801 -> Weather.WeatherState.MainlyClear
        802 -> Weather.WeatherState.PartlyCloudy
        803, 804 -> Weather.WeatherState.Overcast
        in 300..321 -> Weather.WeatherState.DrizzleModerate
        500 -> Weather.WeatherState.RainSlight
        501 -> Weather.WeatherState.RainModerate
        in 502..504 -> Weather.WeatherState.RainHeavy
        511 -> Weather.WeatherState.FreezingRainLight
        in 520..531 -> Weather.WeatherState.RainShowerModerate
        600 -> Weather.WeatherState.SnowSlight
        601 -> Weather.WeatherState.SnowModerate
        602 -> Weather.WeatherState.SnowHeavy
        in 611..622 -> Weather.WeatherState.SnowShowerHeavy
        in 200..232 -> Weather.WeatherState.Thunderstorm
        701, 741 -> Weather.WeatherState.Fog
        else -> Weather.WeatherState.Unknown
    }
}
