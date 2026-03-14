package com.example.weather_app.presentation.alerts
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_app.R
import com.example.weather_app.designsystem.components.WTFloatingButton
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.designsystem.theme.WTTheme
import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import com.example.weather_app.presentation.alerts.components.AlertCard
import com.example.weather_app.presentation.alerts.components.bottomsheet.AddAlertBottomSheet
import com.example.weather_app.presentation.components.EmptyScreen
import com.example.weather_app.presentation.components.ErrorContent
import org.koin.androidx.compose.koinViewModel


@Composable
fun AlertScreen(
    viewModel: AlertsScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val notificationPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showBottomSheet = true
    }

    val exactAlarmSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        showBottomSheet = true
    }


    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AlertsScreenEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }

                AlertsScreenEffect.RequestNotificationPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                AlertsScreenEffect.RequestExactAlarmPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        exactAlarmSettingsLauncher.launch(
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        )
                    }
                }

                AlertsScreenEffect.SaveSuccess -> {
                    showBottomSheet = false
                }
            }
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        modifier = Modifier.background(backgroundGradient)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                AlertsScreenUiState.Loading -> {
                    CircularProgressIndicator(
                        color = Theme.colors.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                AlertsScreenUiState.Empty -> {
                    EmptyScreen(
                        message = stringResource(R.string.no_alerts_yet),
                        painter = R.drawable.no_alerts_clody
                    )
                }

                is AlertsScreenUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { /* Flow auto-retries on resubscription */ }
                    )
                }

                is AlertsScreenUiState.Success -> {
                    AlertsListContent(
                        alerts = state.alerts,
                        onToggleActive = { id, active -> viewModel.toggleAlertActive(id, active) },
                        onDelete = { id -> viewModel.deleteAlert(id) }
                    )
                }
            }

            WTFloatingButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Theme.spacing.large),
                onClick = {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED -> {
                            notificationPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        else -> showBottomSheet = true
                    }
                }
            )
        }
    }

    if (showBottomSheet) {
        AddAlertBottomSheet(
            formState = formState,
            onDismiss = {
                showBottomSheet = false
                viewModel.resetForm()
            },
            onSave = {
                viewModel.saveAlert()
            },
            onStartTimeSelected = viewModel::onStartTimeSelected,
            onEndTimeSelected = viewModel::onEndTimeSelected,
            onAlertTypeChanged = viewModel::onAlertTypeChanged,
            onConditionModeChanged = viewModel::onConditionModeChanged,
            onTemperatureChanged = viewModel::onTemperatureThresholdChanged,
            onWindChanged = viewModel::onWindThresholdChanged,
            onCloudinessChanged = viewModel::onCloudinessThresholdChanged,
        )
    }
}

@Composable
private fun AlertsListContent(
    alerts: List<WeatherAlert>,
    onToggleActive: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Theme.spacing.medium,
                end = Theme.spacing.medium,
                top = Theme.spacing.extraLarge,
                bottom = Theme.spacing.extraLarge
            )
    ) {
        items(items = alerts, key = { it.id }) { alert ->
            AlertCard(
                alert = alert,
                onToggleActive = { newVal -> onToggleActive(alert.id, alert.isActive) },
                onDelete = { onDelete(alert.id) },
                modifier = Modifier.padding(bottom = Theme.spacing.medium)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AlertsScreenPreview() {
    val now = System.currentTimeMillis()
    WTTheme(isDarkTheme = true) {
        AlertsListContent(
            alerts = listOf(
                WeatherAlert(
                    id = 1,
                    startTimeMillis = now + 3_600_000,
                    endTimeMillis = now + 7_200_000,
                    alertType = AlertType.ALARM,
                    conditionMode = AlertConditionMode.CONDITIONS,
                    temperatureThreshold = 30.0,
                    windThreshold = 10.0,
                    cloudinessThreshold = null,
                    isActive = true,
                    lat = 30.0,
                    lon = 31.0,
                    cityName = "Cairo"
                ),
                WeatherAlert(
                    id = 2,
                    startTimeMillis = now - 10_000,
                    endTimeMillis = now - 1_000,
                    alertType = AlertType.NOTIFICATION,
                    conditionMode = AlertConditionMode.ANY,
                    isActive = false,
                    lat = 30.0,
                    lon = 31.0,
                    cityName = "Alexandria"
                ),
                WeatherAlert(
                    id = 3,
                    startTimeMillis = now + 60_000,
                    endTimeMillis = now + 120_000,
                    alertType = AlertType.NOTIFICATION,
                    conditionMode = AlertConditionMode.ANY,
                    isActive = true,
                    lat = 30.0,
                    lon = 31.0,
                    cityName = "Giza"
                ),
            ),
            onToggleActive = { _, _ -> },
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertsScreenLightPreview() {
    val now = System.currentTimeMillis()
    WTTheme(isDarkTheme = false) {
        AlertsListContent(
            alerts = listOf(
                WeatherAlert(
                    id = 1,
                    startTimeMillis = now + 3_600_000,
                    endTimeMillis = now + 7_200_000,
                    alertType = AlertType.ALARM,
                    conditionMode = AlertConditionMode.CONDITIONS,
                    temperatureThreshold = 35.0,
                    windThreshold = 15.0,
                    cloudinessThreshold = 80,
                    isActive = true,
                    lat = 30.0,
                    lon = 31.0,
                    cityName = "Cairo"
                )
            ),
            onToggleActive = { _, _ -> },
            onDelete = {}
        )
    }
}