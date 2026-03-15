package com.example.weather_app.presentation.components.weather

import com.example.weather_app.R
import com.example.weather_app.domain.entity.weather.Weather

fun getWeatherIconRes(state: Weather.WeatherState, isDay: Boolean): Int {
    return when (state) {
        Weather.WeatherState.ClearSky -> if (isDay) R.drawable.clear_sky_day else R.drawable.clear_sky_night
        Weather.WeatherState.MainlyClear -> if (isDay) R.drawable.mainly_clear_day else R.drawable.mainly_clear_night
        Weather.WeatherState.PartlyCloudy -> if (isDay) R.drawable.partly_cloudy_day else R.drawable.partly_cloudy_night
        Weather.WeatherState.Overcast -> if (isDay) R.drawable.overcast_day else R.drawable.overcast_night
        Weather.WeatherState.DrizzleModerate,
        Weather.WeatherState.RainShowerModerate -> if (isDay) R.drawable.drizzle_moderate_day else R.drawable.drizzle_moderate_night
        Weather.WeatherState.RainSlight -> if (isDay) R.drawable.drizzle_light_day else R.drawable.drizzle_light_night
        Weather.WeatherState.RainModerate,
        Weather.WeatherState.RainHeavy -> if (isDay) R.drawable.drizzle_intensity_day else R.drawable.drizzle_intensity_night
        Weather.WeatherState.FreezingRainLight -> if (isDay) R.drawable.freezing_drizzle_light_day else R.drawable.freezing_drizzle_light_night
        Weather.WeatherState.SnowSlight -> if (isDay) R.drawable.snow_fall_light_day else R.drawable.snow_fall_light_night
        Weather.WeatherState.SnowModerate -> if (isDay) R.drawable.snow_fall_moderate_day else R.drawable.snow_fall_moderate_night
        Weather.WeatherState.SnowHeavy -> if (isDay) R.drawable.snow_fall_intensity_day else R.drawable.snow_fall_intensity_night
        Weather.WeatherState.SnowShowerHeavy -> if (isDay) R.drawable.snow_shower_heavy_day else R.drawable.snow_shower_heavy_night
        Weather.WeatherState.Thunderstorm -> if (isDay) R.drawable.thunderstrom_slight_or_moderate_day else R.drawable.thunderstrom_slight_or_moderate_night
        Weather.WeatherState.Fog -> if (isDay) R.drawable.fog_day else R.drawable.fog_night
        else -> if (isDay) R.drawable.clear_sky_day else R.drawable.clear_sky_night
    }
}
