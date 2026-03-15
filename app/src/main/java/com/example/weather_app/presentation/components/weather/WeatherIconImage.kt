package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.weather_app.domain.entity.weather.Weather

@Composable
fun WeatherIconImage(
    state: Weather.WeatherState,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val iconRes = getWeatherIconRes(state, isDay)

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
