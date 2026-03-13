package com.example.weather_app.presentation.alerts.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.presentation.uierror.UiText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertTimePicker(
    label: String,
    selectedMillis: Long?,
    error: UiText?,
    onTimeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("hh:mm aa", Locale.getDefault()) }
    val displayText = selectedMillis
        ?.let { timeFormat.format(Date(it)) }
        ?: stringResource(R.string.tap_to_select_time)

    val hasError = error != null
    val borderColor = if (hasError) Theme.colors.errorColor
    else Theme.colors.primary.copy(alpha = 0.5f)
    val iconTint    = if (hasError) Theme.colors.errorColor else Theme.colors.primary

    Column(modifier = modifier) {
        OutlinedCard(
            onClick = { showDialog = true },
            border = BorderStroke(1.dp, borderColor),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = iconTint
                )
                Spacer(Modifier.width(Theme.spacing.small))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = label,
                        style = Theme.typography.bodySmall,
                        color = iconTint
                    )
                    Text(
                        text  = displayText,
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.textColors.bodyColor
                    )
                }
                if (hasError) {
                    Icon(
                        imageVector = Icons.Outlined.Error,
                        contentDescription = null,
                        tint = Theme.colors.errorColor
                    )
                }
            }
        }

        if (hasError && error != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = error.asString(),
                style = Theme.typography.bodySmall,
                color = Theme.colors.errorColor,
                modifier = Modifier.padding(start = Theme.spacing.small)
            )
        }
    }

    if (showDialog) {
        val initialHour   = selectedMillis?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.HOUR_OF_DAY) } ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val initialMinute = selectedMillis?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.MINUTE) } ?: Calendar.getInstance().get(Calendar.MINUTE)

        val timePickerState = rememberTimePickerState(
            initialHour   = initialHour,
            initialMinute = initialMinute,
            is24Hour      = false
        )

        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor   = Theme.colors.gradientBackground.gradientBackgroundEnd,
            title = {
                Text(
                    text  = label,
                    style = Theme.typography.title,
                    color = Theme.colors.textColors.titleColor
                )
            },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val resolved = resolveTimeMillis(
                            hour   = timePickerState.hour,
                            minute = timePickerState.minute
                        )
                        onTimeSelected(resolved)
                        showDialog = false
                    }
                ) {
                    Text(
                        text  = stringResource(R.string.ok),
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text  = stringResource(R.string.cancel),
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.textColors.bodyColor
                    )
                }
            }
        )
    }
}

/**
 * Converts a picked [hour]/[minute] pair into epoch milliseconds.
 * If the resulting timestamp is in the past, it advances by one day
 * so users never accidentally set a time that has already passed.
 */
private fun resolveTimeMillis(hour: Int, minute: Int): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    if (cal.timeInMillis <= System.currentTimeMillis()) {
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return cal.timeInMillis
}
