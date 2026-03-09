package com.example.weather_app.presentation.settings

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.designsystem.theme.WTTheme
import com.example.weather_app.domain.entity.AppLanguage
import com.example.weather_app.domain.entity.AppTheme
import com.example.weather_app.domain.entity.LocationMode
import com.example.weather_app.domain.entity.TemperatureUnit
import com.example.weather_app.domain.entity.UserPreferences
import com.example.weather_app.domain.entity.WindSpeedUnit
import com.example.weather_app.presentation.components.LocationPickerScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel


@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val prefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    val isGpsEnabled by viewModel.isGpsEnabled.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val appContext = context.applicationContext

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(appContext.getString(event.messageResId))
                }
            }
        }
    }

    SettingsContent(
        prefs = prefs,
        isGpsEnabled = isGpsEnabled,
        snackbarHostState = snackbarHostState,
        onThemeChange = viewModel::setTheme,
        onLanguageChange = viewModel::setLanguage,
        onTemperatureUnitChange = viewModel::setTemperatureUnit,
        onWindSpeedUnitChange = viewModel::setWindSpeedUnit,
        onLocationModeChange = viewModel::setLocationMode,
        onPermissionResult = viewModel::onPermissionResult,
        onManualLocationSaved = viewModel::onManualLocationSaved,
        onRefreshGps = viewModel::refreshGpsState,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun SettingsContent(
    prefs: UserPreferences,
    isGpsEnabled: Boolean,
    snackbarHostState: SnackbarHostState,
    onThemeChange: (AppTheme) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onTemperatureUnitChange: (TemperatureUnit) -> Unit,
    onWindSpeedUnitChange: (WindSpeedUnit) -> Unit,
    onLocationModeChange: (LocationMode) -> Unit,
    onPermissionResult: (Boolean) -> Unit,
    onManualLocationSaved: (Double, Double) -> Unit,
    onRefreshGps: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showMapPicker by remember { mutableStateOf(false) }

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    ) { results -> onPermissionResult(results.values.any { it }) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) onRefreshGps()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd,
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
                .padding(
                    start = Theme.spacing.large,
                    end = Theme.spacing.large,
                    top = Theme.spacing.large,
                ),
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = Theme.typography.headline,
                color = Theme.colors.textColors.titleColor,
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
            ) {
                SettingsSection(title = stringResource(R.string.appearance)) {
                    PreferenceGroup(
                        label = stringResource(R.string.theme),
                        options = AppTheme.entries,
                        selected = prefs.theme,
                        labelOf = { stringResource(it.toResource()) },
                        onSelect = onThemeChange,
                    )
                }

                SettingsSection(title = stringResource(R.string.language)) {
                    PreferenceGroup(
                        label = stringResource(R.string.app_language),
                        options = AppLanguage.entries,
                        selected = prefs.language,
                        labelOf = { stringResource(it.toResource()) },
                        onSelect = onLanguageChange,
                    )
                }

                SettingsSection(title = stringResource(R.string.units)) {
                    PreferenceGroup(
                        label = stringResource(R.string.temperature),
                        options = TemperatureUnit.entries,
                        selected = prefs.temperatureUnit,
                        labelOf = { stringResource(it.toResource()) },
                        onSelect = onTemperatureUnitChange,
                    )
                    Spacer(Modifier.height(Theme.spacing.medium))
                    PreferenceGroup(
                        label = stringResource(R.string.wind_speed),
                        options = WindSpeedUnit.entries,
                        selected = prefs.windSpeedUnit,
                        labelOf = { stringResource(it.toResource()) },
                        onSelect = onWindSpeedUnitChange,
                    )
                }

                SettingsSection(title = stringResource(R.string.location)) {
                    val gpsWarning = prefs.locationMode == LocationMode.GPS && !isGpsEnabled

                    LocationRow(
                        title = stringResource(R.string.use_my_location_gps),
                        subtitle = when {
                            gpsWarning ->
                                stringResource(R.string.gps_is_off_showing_last_known_tap_to_enable_in_settings)

                            prefs.locationMode == LocationMode.GPS ->
                                stringResource(R.string.updates_automatically_when_you_open_the_app)

                            else ->
                                stringResource(R.string.use_device_gps_for_automatic_location_detection)
                        },
                        selected = prefs.locationMode == LocationMode.GPS,
                        isWarning = gpsWarning,
                        onClick = {
                            when {
                                !locationPermissions.allPermissionsGranted ->
                                    locationPermissions.launchMultiplePermissionRequest()

                                !isGpsEnabled -> {
                                    onLocationModeChange(LocationMode.GPS)
                                    context.startActivity(
                                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    )
                                }

                                else -> onLocationModeChange(LocationMode.GPS)
                            }
                        },
                    )

                    Spacer(Modifier.height(Theme.spacing.small))

                    LocationRow(
                        title = stringResource(R.string.fixed_location_map),
                        subtitle = if (prefs.savedLat != null && prefs.savedLon != null)
                            stringResource(
                                R.string.saved_location_format,
                                prefs.savedLat,
                                prefs.savedLon
                            )
                        else
                            stringResource(R.string.tap_to_pick_location),
                        selected = prefs.locationMode == LocationMode.MAP,
                        isWarning = false,
                        onClick = { showMapPicker = true },
                    )
                }
                Spacer(Modifier.height(Theme.spacing.large))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Theme.spacing.medium),
        )
    }
    if (showMapPicker) {
        LocationPickerScreen(
            initialLat = prefs.savedLat ?: 30.0444,
            initialLon = prefs.savedLon ?: 31.2357,
            onLocationSelected = { picked ->
                showMapPicker = false
                onManualLocationSaved(picked.lat, picked.lon)
            },
            onDismiss = { showMapPicker = false },
        )
    }
}


@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
        Text(
            text = title.uppercase(),
            style = Theme.typography.hint,
            color = Theme.colors.textColors.hintColor,
            modifier = Modifier.padding(horizontal = Theme.spacing.small)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.colors.textColors.titleColor.copy(alpha = 0.07f))
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            content()
        }
    }
}

@Composable
private fun <T> PreferenceGroup(
    label: String,
    options: List<T>,
    selected: T,
    labelOf: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = Theme.typography.bodyMedium,
            color = Theme.colors.textColors.bodyColor,
            modifier = Modifier.padding(horizontal = Theme.spacing.small, vertical = 4.dp),
        )
        options.forEach { option ->
            PreferenceRow(
                label = labelOf(option),
                selected = option == selected,
                onClick = { onSelect(option) },
            )
        }
    }
}

@Composable
private fun PreferenceRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (selected) Modifier.border(
                    width = 1.dp,
                    color = Theme.colors.primary.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(10.dp),
                ) else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Theme.spacing.small, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = Theme.typography.bodyMedium,
            color = if (selected) Theme.colors.primary
            else Theme.colors.textColors.bodyColor,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Theme.colors.primary,
                unselectedColor = Theme.colors.textColors.hintColor,
            ),
        )
    }
}

@Composable
private fun LocationRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    isWarning: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = when {
        isWarning -> Theme.colors.warningColor
        selected -> Theme.colors.primary.copy(alpha = 0.55f)
        else -> Theme.colors.primary.copy(alpha = 0f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = Theme.typography.bodyLarge,
                color = when {
                    isWarning -> Theme.colors.warningColor
                    selected -> Theme.colors.primary
                    else -> Theme.colors.textColors.bodyColor
                },
            )
            Text(
                text = subtitle,
                style = Theme.typography.hint,
                color = if (isWarning) Theme.colors.warningColor
                else Theme.colors.textColors.hintColor,
            )
        }
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = if (isWarning) Theme.colors.warningColor
                else Theme.colors.primary,
                unselectedColor = Theme.colors.textColors.hintColor,
            ),
        )
    }
}


private fun AppTheme.toResource() = when (this) {
    AppTheme.LIGHT -> R.string.light
    AppTheme.DARK -> R.string.dark
    AppTheme.SYSTEM -> R.string.system_default
}

private fun AppLanguage.toResource() = when (this) {
    AppLanguage.ENGLISH -> R.string.english
    AppLanguage.ARABIC -> R.string.arabic
}

private fun TemperatureUnit.toResource() = when (this) {
    TemperatureUnit.CELSIUS -> R.string.celsius
    TemperatureUnit.FAHRENHEIT -> R.string.fahrenheit
    TemperatureUnit.KELVIN -> R.string.kelvin
}

private fun WindSpeedUnit.toResource() = when (this) {
    WindSpeedUnit.METER_PER_SEC -> R.string.meters_per_sec
    WindSpeedUnit.MILES_PER_HOUR -> R.string.miles_per_hour

}