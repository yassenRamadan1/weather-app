package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.designsystem.theme.WTTheme
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.presentation.util.getLocalizedCountryName
import java.util.Locale

@Composable
fun CurrentWeatherHeader(
    cityName: String,
    countryCode: String,
    dateFormatted: String,
    timeFormatted: String,
    weatherState: Weather.WeatherState,
    isDay: Boolean,
    temperature: String,
    feelsLike: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    val currentLocale = ConfigurationCompat.getLocales(LocalConfiguration.current).get(0)
        ?: Locale.getDefault()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Theme.spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(2.dp)

                ) {
                    Text(
                        text = getLocalizedCountryName(countryCode, currentLocale),
                        style = Theme.typography.title,
                        color = Theme.colors.textColors.titleColor,
                    )
                    Text(
                        text = cityName,
                        style = Theme.typography.hintMedium,
                        color = Theme.colors.textColors.hintColor,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text(
                    text = dateFormatted,
                    style = Theme.typography.hintMedium,
                    color = Theme.colors.textColors.hintColor,
                )
                Text(
                    text = timeFormatted,
                    style = Theme.typography.bodyMedium,
                    color = Theme.colors.textColors.bodyColor,
                )
            }
        }

        WeatherIconImage(
            state = weatherState,
            isDay = isDay,
            modifier = Modifier.size(120.dp),
            contentDescription = description,
        )

        Text(
            text = temperature,
            style = Theme.typography.headline.copy(fontSize = 56.sp),
            color = Theme.colors.textColors.titleColor,
            textAlign = TextAlign.Center,
        )

        Text(
            text = description.replaceFirstChar { it.uppercase() },
            style = Theme.typography.bodyLarge,
            color = Theme.colors.textColors.bodyColor,
        )

        Text(
            text = stringResource(R.string.feels_like, feelsLike),
            style = Theme.typography.hint,
            color = Theme.colors.textColors.hintColor,
            modifier = Modifier.padding(top = Theme.spacing.extraSmall),
        )
    }
}

@Preview
@Composable
fun CurrentWeatherHeaderPreview() {
    WTTheme {
        PreviewComponentsBox() {

            CurrentWeatherHeader(
                cityName = "New York",
                countryCode = "US",
                dateFormatted = "June 10, 2024",
                timeFormatted = "2:00 PM",
                weatherState = Weather.WeatherState.ClearSky,
                isDay = true,
                temperature = "25°",
                feelsLike = "27°",
                description = "Clear sky",
            )
        }
    }
}
