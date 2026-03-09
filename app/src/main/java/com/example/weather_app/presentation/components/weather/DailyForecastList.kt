package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun DailyForecastList(
    dailyItems: List<DailyForecastDisplayItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
    ) {
        Text(
            text = stringResource(R.string.five_day_forecast),
            style = Theme.typography.bodyLarge,
            color = Theme.colors.textColors.titleColor,
            modifier = Modifier.padding(horizontal = Theme.spacing.medium),
        )
        dailyItems.forEach { item ->
            DailyForecastItem(
                dayName = item.dayName,
                iconCode = item.iconCode,
                minTemp = item.minTemp,
                maxTemp = item.maxTemp,
            )
        }
    }
}

data class DailyForecastDisplayItem(
    val dayName: String,
    val iconCode: String,
    val minTemp: String,
    val maxTemp: String,
)
