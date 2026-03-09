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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
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

// ─── Public entry point ───────────────────────────────────────────────────────

/**
 * Stateful shell — only collects state and routes events.
 * Contains zero layout or business logic.
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val prefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    val isGpsEnabled by viewModel.isGpsEnabled.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
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

// ─── Stateless content ────────────────────────────────────────────────────────

/**
 * Fully stateless — receives all state as parameters and all actions as lambdas.
 * Can be previewed and tested with zero ViewModel involvement.
 */
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showMapPicker by remember { mutableStateOf(false) }

    // Permission launcher — result forwarded to ViewModel via callback
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    ) { results -> onPermissionResult(results.values.any { it }) }

    // Refresh GPS availability every time the user returns from system settings
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
                .padding(WindowInsets.systemBars.asPaddingValues())
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = Theme.spacing.large,
                    vertical = Theme.spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
        ) {

            // Page title
            Text(
                text = stringResource(R.string.settings),
                style = Theme.typography.headline,
                color = Theme.colors.textColors.titleColor,
            )

            // ── Appearance ───────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.appearance)) {
                PreferenceGroup(
                    label = "Theme",
                    options = AppTheme.entries,
                    selected = prefs.theme,
                    labelOf = AppTheme::label,
                    onSelect = onThemeChange,
                )
            }

            // ── Language ─────────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.language)) {
                PreferenceGroup(
                    label = "App Language",
                    options = AppLanguage.entries,
                    selected = prefs.language,
                    labelOf = AppLanguage::label,
                    onSelect = onLanguageChange,
                )
            }

            // ── Units ────────────────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.units)) {
                PreferenceGroup(
                    label = stringResource(R.string.temperature),
                    options = TemperatureUnit.entries,
                    selected = prefs.temperatureUnit,
                    labelOf = TemperatureUnit::label,
                    onSelect = onTemperatureUnitChange,
                )
                Spacer(Modifier.height(Theme.spacing.medium))
                PreferenceGroup(
                    label = stringResource(R.string.wind_speed),
                    options = WindSpeedUnit.entries,
                    selected = prefs.windSpeedUnit,
                    labelOf = WindSpeedUnit::label,
                    onSelect = onWindSpeedUnitChange,
                )
            }

            // ── Location ─────────────────────────────────────────────────────
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
                    title = "Fixed Location (Map)",
                    subtitle = if (prefs.savedLat != null && prefs.savedLon != null)
                        "Saved: %.4f°, %.4f°".format(prefs.savedLat, prefs.savedLon)
                    else
                        "Tap to pick a location on the map.",
                    selected = prefs.locationMode == LocationMode.MAP,
                    isWarning = false,
                    onClick = { showMapPicker = true },
                )
            }

            // Bottom breathing room above the nav bar
            Spacer(Modifier.height(Theme.spacing.large))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Theme.spacing.medium),
        )
    }

    // Full-screen overlay — rendered at Box root so it covers everything
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

// ─── Reusable design-system components ───────────────────────────────────────

/**
 * Titled card wrapping a group of related preference rows.
 */
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
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.colors.textColors.titleColor.copy(alpha = 0.07f))
                .padding(Theme.spacing.medium),
        ) {
            content()
        }
    }
}

/**
 * Generic single-choice group rendered as radio rows.
 * Works for any enum-like list — Theme, Language, TemperatureUnit, WindSpeedUnit.
 *
 * @param T        Any type with meaningful equality.
 * @param label    Group heading shown above the options.
 * @param options  Full list of choices.
 * @param selected Currently active value.
 * @param labelOf  Maps a [T] to its display string.
 * @param onSelect Callback with the newly selected value.
 */
@Composable
private fun <T> PreferenceGroup(
    label: String,
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = Theme.typography.bodyMedium,
            color = Theme.colors.textColors.bodyColor,
            modifier = Modifier.padding(bottom = 4.dp),
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

/**
 * Single preference row with a radio button and highlight border when selected.
 */
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
            .padding(horizontal = Theme.spacing.small, vertical = 10.dp),
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

/**
 * Location-mode row with two-line content and an optional warning tint.
 */
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
        selected  -> Theme.colors.primary.copy(alpha = 0.55f)
        else      -> Theme.colors.primary.copy(alpha = 0f) // transparent, keeps layout stable
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
                    selected  -> Theme.colors.primary
                    else      -> Theme.colors.textColors.bodyColor
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

// ─── Presentation-layer display labels ───────────────────────────────────────
// These live here, NOT on the domain entity, to keep the domain layer free
// of any Android/UI concerns.

private fun AppTheme.label() = when (this) {
    AppTheme.LIGHT  -> "Light"
    AppTheme.DARK   -> "Dark"
    AppTheme.SYSTEM -> "System default"
}

private fun AppLanguage.label() = when (this) {
    AppLanguage.ENGLISH -> "English"
    AppLanguage.ARABIC  -> "العربية"
}

private fun TemperatureUnit.label() = when (this) {
    TemperatureUnit.CELSIUS    -> "Celsius (°C)"
    TemperatureUnit.FAHRENHEIT -> "Fahrenheit (°F)"
    TemperatureUnit.KELVIN     -> "Kelvin (K)"
}

private fun WindSpeedUnit.label() = when (this) {
    WindSpeedUnit.METER_PER_SEC  -> "Meters per second (m/s)"
    WindSpeedUnit.MILES_PER_HOUR -> "Miles per hour (mph)"
}