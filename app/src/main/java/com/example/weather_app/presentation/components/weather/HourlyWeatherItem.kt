package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun HourlyWeatherItem(
    time: String,
    iconCode: String,
    temperature: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(Theme.shapes.medium)
            .background(Theme.colors.textColors.titleColor.copy(alpha = 0.07f))
            .padding(horizontal = Theme.spacing.small, vertical = Theme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
    ) {
        Text(
            text = time,
            style = Theme.typography.hint,
            color = Theme.colors.textColors.hintColor,
        )
        WeatherIconImage(
            iconCode = iconCode,
            modifier = Modifier.size(36.dp),
        )
        Text(
            text = temperature,
            style = Theme.typography.bodyMedium,
            color = Theme.colors.textColors.titleColor,
        )
    }
}
