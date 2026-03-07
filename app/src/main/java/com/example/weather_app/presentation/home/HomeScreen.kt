package com.example.weather_app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weather_app.domain.entity.Weather
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(30.0444, 31.2357) {
        viewModel.loadWeather(30.0444, 31.2357)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> CircularProgressIndicator()

            is HomeUiState.Success -> WeatherContent(state.weather)

            is HomeUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = { viewModel.loadWeather(30.0444, 31.2357) }
            )

            is HomeUiState.LocationPermissionRequired -> LocationPermissionPrompt()
        }
    }
}

@Composable
private fun WeatherContent(weather: Weather) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text(text = "${weather.cityName}, ${weather.countryCode}",
            style = MaterialTheme.typography.headlineMedium)
        Text(text = "${weather.temperature}°",
            style = MaterialTheme.typography.displayLarge)
        Text(text = weather.description,
            style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            WeatherStat("Humidity", "${weather.humidity}%")
            WeatherStat("Wind", "${weather.windSpeed} m/s")
            WeatherStat("Pressure", "${weather.pressure} hPa")
        }
    }
}

@Composable
private fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun LocationPermissionPrompt() {
    Text("Please enable location to continue.")
}