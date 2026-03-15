package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.designsystem.theme.WTTheme
import com.example.weather_app.domain.entity.weather.Weather

@Composable
fun DailyForecastItem(
    dayName: String,
    weatherState: Weather.WeatherState,
    isDay: Boolean,
    minTemp: String,
    maxTemp: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.textColors.titleColor.copy(alpha = 0.07f))
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.extraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = dayName,
            style = Theme.typography.bodyLarge,
            color = Theme.colors.textColors.bodyColor,
            modifier = Modifier.weight(1f),
        )
        WeatherIconImage(
            state = weatherState,
            isDay = isDay,
            modifier = Modifier.size(36.dp),
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = maxTemp,
                style = Theme.typography.bodyLarge,
                color = Theme.colors.textColors.titleColor,
            )
            Text(
                text = " / ",
                style = Theme.typography.bodyLarge,
                color = Theme.colors.textColors.hintColor,
            )
            Text(
                text = minTemp,
                style = Theme.typography.hint,
                color = Theme.colors.textColors.hintColor,
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DailyForecastItemPreview() {
    WTTheme {
        PreviewComponentsBox(){
            DailyForecastItem(
                dayName = "Monday",
                weatherState = Weather.WeatherState.ClearSky,
                isDay = true,
                minTemp = "15°",
                maxTemp = "25°",
            )
        }

    }
}
