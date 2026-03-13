package com.example.weather_app.presentation.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.domain.entity.user.LocationSource
import com.example.weather_app.presentation.components.CacheBanner
import com.example.weather_app.presentation.components.ErrorContent
import com.example.weather_app.presentation.components.weather.WeatherDisplayContent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val hasLaunchedRequest by viewModel.hasLaunchedPermissionRequest.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.userMessage) {
        viewModel.userMessage.collect { messageResId ->
            Toast.makeText(context, context.getString(messageResId), Toast.LENGTH_LONG).show()
        }
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted || hasLaunchedRequest) {
            val isPermanentlyDenied = !permissionState.allPermissionsGranted &&
                    permissionState.permissions.all { perm ->
                        !perm.status.isGranted && !perm.status.shouldShowRationale
                    }
            viewModel.onPermissionResult(
                granted = permissionState.allPermissionsGranted,
                isPermanentlyDenied = isPermanentlyDenied
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.resolveLocationAndLoadWeather()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
            is HomeUiState.Loading -> HomeLoadingIndicator()
            
            is HomeUiState.Success -> HomeSuccessContent(
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = viewModel::onRefresh,
                onEnableGps = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            )

            is HomeUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::resolveLocationAndLoadWeather
            )

            is HomeUiState.NeedLocationPermission -> {
                val isPermanentlyDenied = hasLaunchedRequest &&
                        permissionState.permissions.all { perm ->
                            !perm.status.isGranted && !perm.status.shouldShowRationale
                        }
                PermissionPrompt(
                    isPermanentlyDenied = isPermanentlyDenied,
                    onRequestPermission = {
                        viewModel.onPermissionRequestLaunched()
                        permissionState.launchMultiplePermissionRequest()
                    },
                    onOpenAppSettings = {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }
                )
            }

            is HomeUiState.GpsDisabled -> GpsDisabledPrompt(
                onEnableGps = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            )

            is HomeUiState.GpsNoFix -> GpsNoFixPrompt(
                onRetry = viewModel::resolveLocationAndLoadWeather
            )

            is HomeUiState.NeedManualLocation -> NeedManualLocationPrompt(
                onOpenMap = { /* Navigate to Map */ }
            )
        }
    }
}

@Composable
private fun HomeLoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(48.dp),
        color = Theme.colors.primaryIconColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSuccessContent(
    state: HomeUiState.Success,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onEnableGps: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = state.isFromCache) {
                CacheBanner()
            }
            AnimatedVisibility(visible = state.isStaleLocation && !state.isFromCache) {
                StaleLocationBanner(
                    source = state.locationSource,
                    onEnableGps = onEnableGps
                )
            }
            WeatherDisplayContent(
                currentWeather = state.currentWeather,
                hourlyForecast = state.hourlyForecast,
                dailyForecast = state.dailyForecast,
                currentDateFormatted = state.currentDateFormatted,
                currentTimeFormatted = state.currentTimeFormatted,
                temperatureUnit = state.temperatureUnit,
                windSpeedUnit = state.windSpeedUnit,
                isStaleLocation = state.isStaleLocation,
                onEnableGps = onEnableGps,
            )
        }
    }
}

@Composable
private fun StaleLocationBanner(source: LocationSource, onEnableGps: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Theme.colors.warningColor.copy(alpha = 0.15f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Theme.colors.warningColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = when (source) {
                    LocationSource.LAST_KNOWN -> stringResource(R.string.gps_is_off_showing_last_known_location)
                    LocationSource.SAVED_PREFERENCES -> stringResource(R.string.gps_is_off_showing_saved_location)
                    else -> stringResource(R.string.using_cached_location)
                },
                style = Theme.typography.bodyMedium,
                color = Theme.colors.textColors.bodyColor,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onEnableGps,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    stringResource(R.string.enable_gps),
                    style = Theme.typography.bodyMedium,
                    color = Theme.colors.warningColor
                )
            }
        }
    }
}

@Composable
private fun PermissionPrompt(
    isPermanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = if (isPermanentlyDenied)
                painterResource(R.drawable.location_disabled)
            else
                rememberVectorPainter(Icons.Default.LocationOn),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Theme.colors.buttonColor
        )

        Text(
            text = if (isPermanentlyDenied) stringResource(R.string.location_access_blocked)
            else stringResource(R.string.location_permission_needed),
            style = Theme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isPermanentlyDenied)
                stringResource(R.string.you_ve_blocked_location_access_open_app_settings_permissions_location_allow)
            else
                stringResource(R.string.weather_needs_your_location_to_show_accurate_local_forecasts),
            textAlign = TextAlign.Center,
            style = Theme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = if (isPermanentlyDenied) onOpenAppSettings else onRequestPermission,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.buttonColor)
        ) {
            Text(
                if (isPermanentlyDenied) stringResource(R.string.open_app_settings)
                else stringResource(R.string.allow_location)
            )
        }
    }
}

@Composable
private fun GpsDisabledPrompt(onEnableGps: () -> Unit) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.gps_disable),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Theme.colors.errorColor
        )
        Text(
            stringResource(R.string.gps_is_turned_off),
            style = Theme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.enable_location_services_on_your_device_to_get_weather_for_your_current_location),
            textAlign = TextAlign.Center,
            style = Theme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onEnableGps,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.buttonColor)
        ) {
            Text(stringResource(R.string.enable_location_services))
        }
    }
}

@Composable
private fun GpsNoFixPrompt(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.gps_disable),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Theme.colors.warningColor
        )
        Text(
            stringResource(R.string.gps_no_fix_title),
            style = Theme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.gps_no_fix_body),
            textAlign = TextAlign.Center,
            style = Theme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.buttonColor)
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun NeedManualLocationPrompt(onOpenMap: () -> Unit) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(72.dp))
        Text(stringResource(R.string.choose_a_location), style = Theme.typography.headline)
        Text(
            stringResource(R.string.you_selected_manual_mode_pick_your_city_on_the_map),
            textAlign = TextAlign.Center,
            style = Theme.typography.bodyLarge
        )
        Button(onClick = onOpenMap, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.pick_on_map))
        }
    }
}
