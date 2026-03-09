package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun WeatherStatsRow(
    humidity: String,
    windSpeed: String,
    pressure: String,
    clouds: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        WeatherStatItem(
            label = stringResource(R.string.humidity),
            value = humidity,
            icon = Icons.Default.WaterDrop,
        )
        WeatherStatItem(
            label = stringResource(R.string.wind),
            value = windSpeed,
            icon = Icons.Default.Air,
        )
        WeatherStatItem(
            label = stringResource(R.string.pressure),
            value = pressure,
            icon = Icons.Default.Compress,
        )
        WeatherStatItem(
            label = stringResource(R.string.clouds),
            value = clouds,
            icon = Icons.Default.Cloud,
        )
    }
}
