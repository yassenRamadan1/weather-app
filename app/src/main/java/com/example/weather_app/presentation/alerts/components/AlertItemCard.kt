package com.example.weather_app.presentation.alerts.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.domain.entity.alert.WeatherAlert
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.let

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlertCard(
    alert: WeatherAlert,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm aa", Locale.getDefault()) }
    val now = System.currentTimeMillis()
    val isExpired = alert.endTimeMillis < now
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Theme.colors.errorColor
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.delete_alert),
                    style = Theme.typography.bodyLarge
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_alert_confirmation),
                    style = Theme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = Theme.colors.errorColor
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isExpired || !alert.isActive) 0.65f else 1f),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.cardBackgroundColor
        ),
        shape = Theme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AlertTypeBadge(alert.alertType)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isExpired) {
                        Switch(
                            checked = alert.isActive,
                            onCheckedChange = onToggleActive,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Theme.colors.onBodyColor,
                                checkedTrackColor = Theme.colors.primary,
                                uncheckedThumbColor = Theme.colors.textColors.hintColor,
                                uncheckedTrackColor = Theme.colors.textColors.hintColor.copy(alpha = 0.3f)
                            )
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete_alert),
                            tint = Theme.colors.errorColor
                        )
                    }
                }
            }

            Spacer(Modifier.height(Theme.spacing.small))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Theme.colors.primaryIconColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${timeFormat.format(Date(alert.startTimeMillis))}  →  ${
                        timeFormat.format(
                            Date(alert.endTimeMillis)
                        )
                    }",
                    style = Theme.typography.bodyMedium,
                    color = Theme.colors.textColors.bodyColor
                )
            }
            alert.cityName?.let { city ->
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Theme.colors.primaryIconColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = city,
                        style = Theme.typography.bodySmall,
                        color = Theme.colors.textColors.hintColor
                    )
                }
            }

            Spacer(Modifier.height(Theme.spacing.small))
            when (alert.conditionMode) {
                AlertConditionMode.ANY -> {
                    Text(
                        text = stringResource(R.string.any_weather_condition),
                        style = Theme.typography.bodySmall,
                        color = Theme.colors.textColors.hintColor
                    )
                }

                AlertConditionMode.CONDITIONS -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        alert.temperatureThreshold?.let {
                            ConditionChip(label = "🌡 ≥ ${it.toInt()}°")
                        }
                        alert.windThreshold?.let {
                            ConditionChip(label = "💨 ≥ $it m/s")
                        }
                        alert.cloudinessThreshold?.let {
                            ConditionChip(label = "☁ ≥ $it %")
                        }
                    }
                }
            }
            AnimatedVisibility(visible = isExpired) {
                Column {
                    Spacer(Modifier.height(Theme.spacing.small))
                    Surface(
                        color = Theme.colors.errorColor.copy(alpha = 0.12f),
                        shape = Theme.shapes.small
                    ) {
                        Text(
                            text = stringResource(R.string.alert_expired),
                            style = Theme.typography.bodySmall,
                            color = Theme.colors.errorColor,
                            modifier = Modifier.padding(
                                horizontal = Theme.spacing.small,
                                vertical = 2.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertTypeBadge(type: AlertType) {
    val icon = when (type) {
        AlertType.ALARM -> Icons.Outlined.Alarm
        AlertType.NOTIFICATION -> Icons.Outlined.NotificationsActive
    }
    val labelRes = when (type) {
        AlertType.ALARM -> R.string.alarm
        AlertType.NOTIFICATION -> R.string.notification
    }
    Surface(
        color = Theme.colors.primary.copy(alpha = 0.15f),
        shape = Theme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Theme.spacing.small, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(labelRes),
                style = Theme.typography.bodySmall,
                color = Theme.colors.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ConditionChip(label: String) {
    Surface(
        color = Theme.colors.cardBackgroundColor.copy(alpha = 0.6f),
        shape = Theme.shapes.small,
        tonalElevation = 2.dp
    ) {
        Text(
            text = label,
            style = Theme.typography.bodySmall,
            color = Theme.colors.textColors.bodyColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}