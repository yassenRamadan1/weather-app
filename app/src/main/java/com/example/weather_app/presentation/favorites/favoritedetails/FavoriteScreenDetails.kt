package com.example.weather_app.presentation.favorites.favoritedetails

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.domain.entity.DailyForecast
import com.example.weather_app.domain.entity.HourlyWeather
import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.entity.Weather
import com.example.weather_app.domain.entity.WindSpeedUnit
import com.example.weather_app.presentation.components.CacheBanner
import com.example.weather_app.presentation.components.ErrorContent
import com.example.weather_app.presentation.components.weather.WeatherDisplayContent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailsScreen(
    viewModel: FavoriteDetailsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { messageResId ->
            when (messageResId) {
                null -> {}
                is FavoriteDetailsEffect.ShowError -> {
                    Toast.makeText(
                        context,
                        context.getString(messageResId.message),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }
    val brushBackGround = Brush.verticalGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = brushBackGround)
            .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is FavoriteDetailsScreenUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.onRefresh() }
                )
            }
            FavoriteDetailsScreenUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Theme.colors.primary
                    )
                }
            }
            is FavoriteDetailsScreenUiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {viewModel.onRefresh()},
                    modifier = Modifier.fillMaxSize()
                ) {
                    FavoriteDetailsScreenContent(
                        currentWeather = state.currentWeather,
                        hourlyForecast = state.hourlyForecast,
                        dailyForecast = state.dailyForecast,
                        currentDateFormatted = state.currentDateFormatted,
                        currentTimeFormatted = state.currentTimeFormatted,
                        temperatureUnit = state.temperatureUnit,
                        windSpeedUnit = state.windSpeedUnit,
                        isFromCache = state.isFromCache,
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteDetailsScreenContent(
    currentWeather: Weather,
    hourlyForecast: List<HourlyWeather>,
    dailyForecast: List<DailyForecast>,
    currentDateFormatted: String,
    currentTimeFormatted: String,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    isFromCache: Boolean,
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.small)
                    .clickable { onNavigateBack() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Theme.colors.onBodyColor
                )
                Text(
                    text = stringResource(R.string.weather_details),
                    color = Theme.colors.onBodyColor,
                    style = Theme.typography.bodyLarge,
                    modifier = Modifier.padding(start = Theme.spacing.small)
                )
            }
        AnimatedVisibility(visible = isFromCache) {
            CacheBanner()
        }
        WeatherDisplayContent(
            currentWeather = currentWeather,
            hourlyForecast = hourlyForecast,
            dailyForecast = dailyForecast,
            currentDateFormatted = currentDateFormatted,
            currentTimeFormatted = currentTimeFormatted,
            temperatureUnit = temperatureUnit,
            windSpeedUnit = windSpeedUnit,
            isStaleLocation = false,
            onEnableGps = {
            },
        )
    }

}