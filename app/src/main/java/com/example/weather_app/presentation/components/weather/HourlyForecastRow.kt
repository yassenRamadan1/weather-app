package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun HourlyForecastRow(
    hourlyItems: List<HourlyForecastDisplayItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        Text(
            text = stringResource(R.string.todays_hourly_forecast),
            style = Theme.typography.bodyLarge,
            color = Theme.colors.textColors.titleColor,
            modifier = Modifier.padding(horizontal = Theme.spacing.medium),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = Theme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            items(hourlyItems) { item ->
                HourlyWeatherItem(
                    time = item.time,
                    iconCode = item.iconCode,
                    temperature = item.temperature,
                )
            }
        }
    }
}

data class HourlyForecastDisplayItem(
    val time: String,
    val iconCode: String,
    val temperature: String,
)
