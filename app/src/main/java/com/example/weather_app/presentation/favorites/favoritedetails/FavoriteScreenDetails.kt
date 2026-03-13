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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.domain.entity.weather.DailyForecast
import com.example.weather_app.domain.entity.weather.HourlyWeather
import com.example.weather_app.domain.entity.user.TemperatureUnit
import com.example.weather_app.domain.entity.weather.Weather
import com.example.weather_app.domain.entity.user.WindSpeedUnit
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

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteDetailsEffect.ShowError -> {
                    Toast.makeText(
                        context,
                        context.getString(effect.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
                null -> {}
            }
        }
    }

    val startColor = Theme.colors.gradientBackground.gradientBackgroundStart
    val endColor = Theme.colors.gradientBackground.gradientBackgroundEnd
    val brushBackground = remember(startColor, endColor) {
        Brush.verticalGradient(
            colors = listOf(startColor, endColor)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = brushBackground)
            .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is FavoriteDetailsScreenUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Theme.colors.primaryIconColor
                )
            }

            is FavoriteDetailsScreenUiState.Success -> {
                FavoriteDetailsSuccessContent(
                    state = state,
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::onRefresh,
                    onNavigateBack = onNavigateBack
                )
            }

            is FavoriteDetailsScreenUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = viewModel::onRefresh
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteDetailsSuccessContent(
    state: FavoriteDetailsScreenUiState.Success,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FavoriteDetailsTopBar(onNavigateBack = onNavigateBack)
            
            AnimatedVisibility(visible = state.isFromCache) {
                CacheBanner()
            }
            
            WeatherDisplayContent(
                currentWeather = state.currentWeather,
                hourlyForecast = state.hourlyForecast,
                dailyForecast = state.dailyForecast,
                currentDateFormatted = state.currentDateFormatted,
                currentTimeFormatted = state.currentTimeFormatted,
                temperatureUnit = state.temperatureUnit,
                windSpeedUnit = state.windSpeedUnit,
                isStaleLocation = false,
                onEnableGps = { },
            )
        }
    }
}

@Composable
private fun FavoriteDetailsTopBar(onNavigateBack: () -> Unit) {
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
            contentDescription = null,
            tint = Theme.colors.onBodyColor
        )
        Text(
            text = stringResource(R.string.weather_details),
            color = Theme.colors.onBodyColor,
            style = Theme.typography.bodyLarge,
            modifier = Modifier.padding(start = Theme.spacing.small)
        )
    }
}
