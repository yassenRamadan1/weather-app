package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.entity.HourlyWeather
import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.entity.Weather
import com.example.weather_app.domain.entity.WindSpeedUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherDisplayContent(
    currentWeather: Weather,
    hourlyForecast: List<HourlyWeather>,
    dailyForecast: List<DailyForecast>,
    currentDateFormatted: String,
    currentTimeFormatted: String,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    isStaleLocation: Boolean,
    onEnableGps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tempSymbol = temperatureUnit.symbol
    val windSymbol = windSpeedUnit.symbol

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
    ) {
        CurrentWeatherHeader(
            cityName = currentWeather.cityName,
            dateFormatted = currentDateFormatted,
            timeFormatted = currentTimeFormatted,
            iconCode = currentWeather.iconCode,
            temperature = "${currentWeather.temperature.roundToInt()}$tempSymbol",
            feelsLike = "${currentWeather.feelsLike.roundToInt()}$tempSymbol",
            description = currentWeather.description,
        )

        WeatherStatsRow(
            humidity = "${currentWeather.humidity}%",
            windSpeed = "${currentWeather.windSpeed} $windSymbol",
            pressure = "${currentWeather.pressure} hPa",
            clouds = "${currentWeather.cloudiness}%",
        )

        if (hourlyForecast.isNotEmpty()) {
            val hourlyDisplayItems = hourlyForecast.map { hourly ->
                val time = Instant.ofEpochSecond(hourly.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
                HourlyForecastDisplayItem(
                    time = time,
                    iconCode = hourly.iconCode,
                    temperature = "${hourly.temperature.roundToInt()}$tempSymbol",
                )
            }
            HourlyForecastRow(hourlyItems = hourlyDisplayItems)
        }

        if (dailyForecast.isNotEmpty()) {
            val dailyDisplayItems = dailyForecast.map { daily ->
                val dayName = Instant.ofEpochSecond(daily.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
                DailyForecastDisplayItem(
                    dayName = dayName,
                    iconCode = daily.iconCode,
                    minTemp = "${daily.minTemp.roundToInt()}$tempSymbol",
                    maxTemp = "${daily.maxTemp.roundToInt()}$tempSymbol",
                )
            }
            DailyForecastList(dailyItems = dailyDisplayItems)
        }

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))
    }
}
