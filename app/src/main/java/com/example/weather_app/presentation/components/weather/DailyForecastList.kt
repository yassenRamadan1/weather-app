package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun DailyForecastList(
    dailyItems: List<DailyForecastDisplayItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        Text(
            text = stringResource(R.string.five_day_forecast),
            style = Theme.typography.bodyLarge,
            color = Theme.colors.textColors.titleColor,
            modifier = Modifier,
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
@Preview
@Composable
fun DailyForecastListPreview() {
    val sampleItems = listOf(
        DailyForecastDisplayItem("Monday", "01d", "15°C", "25°C"),
        DailyForecastDisplayItem("Tuesday", "02d", "17°C", "27°C"),
        DailyForecastDisplayItem("Wednesday", "03d", "14°C", "24°C"),
        DailyForecastDisplayItem("Thursday", "04d", "16°C", "26°C"),
        DailyForecastDisplayItem("Friday", "01d", "13°C", "23°C"),
    )
    PreviewComponentsBox() {
        DailyForecastList(dailyItems = sampleItems)
    }

}