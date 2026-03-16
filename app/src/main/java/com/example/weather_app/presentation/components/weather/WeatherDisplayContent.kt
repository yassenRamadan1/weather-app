package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.domain.entity.user.TemperatureUnit
import com.example.weather_app.domain.entity.user.WindSpeedUnit
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.presentation.util.getLocalizedDayName
import com.example.weather_app.presentation.util.getLocalizedTime
import com.example.weather_app.presentation.util.toUnitRes
import kotlin.math.roundToInt

private val FADE_SCROLL_THRESHOLD = 280.dp
private const val MAX_BACKGROUND_SCALE = 1.3f
private const val BASE_BACKGROUND_ALPHA = 0.3f

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
    val locale = LocalConfiguration.current.locales[0]
    val density = LocalDensity.current

    val tempSymbol = stringResource(temperatureUnit.toUnitRes())
    val windSymbol = stringResource(windSpeedUnit.toUnitRes())
    val pressureUnit = stringResource(R.string.unit_pressure)
    val scrollState = rememberScrollState()
    val fadeThresholdPx = with(density) { FADE_SCROLL_THRESHOLD.toPx() }
    val collapseFraction by remember {
        derivedStateOf {
            (scrollState.value / fadeThresholdPx).coerceIn(0f, 1f)
        }
    }

    val headerAlpha by remember { derivedStateOf { 1f - collapseFraction } }
    val backgroundAlpha by remember { derivedStateOf { BASE_BACKGROUND_ALPHA * (1f - collapseFraction) } }
    val backgroundScale by remember { derivedStateOf { 1f + (collapseFraction * (MAX_BACKGROUND_SCALE - 1f)) } }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_cloud),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = (-20).dp)
                    .graphicsLayer {
                        scaleX = backgroundScale
                        scaleY = backgroundScale
                        alpha = backgroundAlpha
                    },
                contentScale = ContentScale.Crop
            )

            CurrentWeatherHeader(
                cityName = currentWeather.cityName,
                dateFormatted = currentDateFormatted,
                timeFormatted = currentTimeFormatted,
                weatherState = currentWeather.weatherState,
                isDay = currentWeather.isDay,
                temperature = "${currentWeather.temperature.roundToInt()}$tempSymbol",
                feelsLike = "${currentWeather.feelsLike.roundToInt()}$tempSymbol",
                description = currentWeather.description,
                countryCode = currentWeather.countryCode ?: "",
                // 5. Apply the fade-out alpha to the header content
                modifier = Modifier.graphicsLayer {
                    alpha = headerAlpha
                }
            )
        }

        WeatherStatsRow(
            humidity = "${currentWeather.humidity}%",
            windSpeed = "${currentWeather.windSpeed} $windSymbol",
            pressure = "${currentWeather.pressure} $pressureUnit",
            clouds = "${currentWeather.cloudiness}%",
        )

        if (hourlyForecast.isNotEmpty()) {
            val hourlyDisplayItems = hourlyForecast.map { hourly ->
                HourlyForecastDisplayItem(
                    time = getLocalizedTime(hourly.timestamp, locale = locale),
                    weatherState = hourly.weatherState,
                    isDay = hourly.isDay,
                    temperature = "${hourly.temperature.roundToInt()}$tempSymbol",
                )
            }
            HourlyForecastRow(hourlyItems = hourlyDisplayItems)
        }

        if (dailyForecast.isNotEmpty()) {
            val dailyMaxTemps = dailyForecast.map { it.maxTemp.toDouble() }
            val daysOfWeek = dailyForecast.map { daily ->
                getLocalizedDayName(daily.timestamp, locale = locale)
            }

            WeeklyTemperatureLineChart(
                dailyMaxTemps = dailyMaxTemps,
                daysOfWeek = daysOfWeek.take(5),
                modifier = Modifier.padding(horizontal = Theme.spacing.medium)
            )

            val dailyDisplayItems = dailyForecast.map { daily ->
                DailyForecastDisplayItem(
                    dayName = getLocalizedDayName(daily.timestamp, locale = locale),
                    weatherState = daily.weatherState,
                    isDay = daily.isDay,
                    minTemp = "${daily.minTemp.roundToInt()}$tempSymbol",
                    maxTemp = "${daily.maxTemp.roundToInt()}$tempSymbol",
                )
            }
            DailyForecastList(dailyItems = dailyDisplayItems)
        }

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))
    }
}