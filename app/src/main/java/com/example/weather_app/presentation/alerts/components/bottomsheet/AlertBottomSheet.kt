package com.example.weather_app.presentation.alerts.components.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.domain.entity.alert.AlertConditionMode
import com.example.weather_app.domain.entity.alert.AlertType
import com.example.weather_app.presentation.alerts.components.AlertTimePicker
import com.example.weather_app.presentation.alerts.components.ThresholdInputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertBottomSheet(
    formState: AddAlertFormState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onStartTimeSelected: (Long) -> Unit,
    onEndTimeSelected: (Long) -> Unit,
    onAlertTypeChanged: (AlertType) -> Unit,
    onConditionModeChanged: (AlertConditionMode) -> Unit,
    onTemperatureChanged: (String) -> Unit,
    onWindChanged: (String) -> Unit,
    onCloudinessChanged: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = Theme.colors.gradientBackground.gradientBackgroundEnd,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Theme.colors.textColors.hintColor)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing.medium)
                .padding(bottom = 40.dp) // clear nav bar
        ) {

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text     = stringResource(R.string.add_weather_alert),
                style    = Theme.typography.title,
                color    = Theme.colors.textColors.titleColor,
                modifier = Modifier.padding(bottom = Theme.spacing.medium)
            )

            // ── Start time picker ─────────────────────────────────────────────
            AlertTimePicker(
                label            = stringResource(R.string.start_duration),
                selectedMillis   = formState.startTimeMillis,
                error            = formState.startError,
                onTimeSelected   = onStartTimeSelected,
                modifier         = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            // ── End time picker ───────────────────────────────────────────────
            AlertTimePicker(
                label            = stringResource(R.string.end_duration),
                selectedMillis   = formState.endTimeMillis,
                error            = formState.endError,
                onTimeSelected   = onEndTimeSelected,
                modifier         = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.large))
            HorizontalDivider(color = Theme.colors.textColors.hintColor.copy(alpha = 0.2f))
            Spacer(Modifier.height(Theme.spacing.medium))

            // ── Condition mode toggle ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = if (formState.conditionMode == AlertConditionMode.ANY)
                            stringResource(R.string.any_weather)
                        else
                            stringResource(R.string.conditions),
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.textColors.bodyColor
                    )
                    Text(
                        text  = if (formState.conditionMode == AlertConditionMode.ANY)
                            stringResource(R.string.any_weather_desc)
                        else
                            stringResource(R.string.conditions_desc),
                        style = Theme.typography.bodySmall,
                        color = Theme.colors.textColors.hintColor
                    )
                }
                Switch(
                    checked = formState.conditionMode == AlertConditionMode.CONDITIONS,
                    onCheckedChange = { checked ->
                        onConditionModeChanged(
                            if (checked) AlertConditionMode.CONDITIONS else AlertConditionMode.ANY
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor   = Theme.colors.onBodyColor,
                        checkedTrackColor   = Theme.colors.primary,
                        uncheckedThumbColor = Theme.colors.textColors.hintColor,
                        uncheckedTrackColor = Theme.colors.textColors.hintColor.copy(alpha = 0.3f)
                    )
                )
            }

            // ── Threshold fields (animated) ───────────────────────────────────
            AnimatedVisibility(
                visible = formState.conditionMode == AlertConditionMode.CONDITIONS,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(Theme.spacing.medium))

                    ThresholdInputField(
                        label         = stringResource(R.string.temperature_threshold),
                        value         = formState.temperatureThreshold,
                        suffix        = "°",
                        onValueChange = onTemperatureChanged,
                        allowNegative = true,
                        modifier      = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(Theme.spacing.small))

                    ThresholdInputField(
                        label         = stringResource(R.string.wind_threshold),
                        value         = formState.windThreshold,
                        suffix        = "m/s",
                        onValueChange = onWindChanged,
                        allowNegative = false,
                        modifier      = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(Theme.spacing.small))

                    ThresholdInputField(
                        label         = stringResource(R.string.cloudiness_threshold),
                        value         = formState.cloudinessThreshold,
                        suffix        = "%",
                        onValueChange = onCloudinessChanged,
                        allowNegative = false,
                        modifier      = Modifier.fillMaxWidth()
                    )

                    // Condition-level error (e.g. "set at least one threshold")
                    formState.conditionError?.let { err ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = err.asString(),
                            style = Theme.typography.bodySmall,
                            color = Theme.colors.errorColor,
                            modifier = Modifier.padding(start = Theme.spacing.small)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Theme.spacing.large))
            HorizontalDivider(color = Theme.colors.textColors.hintColor.copy(alpha = 0.2f))
            Spacer(Modifier.height(Theme.spacing.medium))

            // ── Alert type radio group ─────────────────────────────────────────
            Text(
                text  = stringResource(R.string.notify_me_by),
                style = Theme.typography.bodyMedium,
                color = Theme.colors.textColors.bodyColor
            )

            Spacer(Modifier.height(Theme.spacing.small))

            Row(modifier = Modifier.fillMaxWidth()) {
                AlertType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Theme.spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = formState.alertType == type,
                            onClick  = { onAlertTypeChanged(type) },
                            colors   = RadioButtonDefaults.colors(
                                selectedColor   = Theme.colors.primary,
                                unselectedColor = Theme.colors.textColors.hintColor
                            )
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text  = stringResource(
                                when (type) {
                                    AlertType.ALARM        -> R.string.alarm
                                    AlertType.NOTIFICATION -> R.string.notification
                                }
                            ),
                            style = Theme.typography.bodyMedium,
                            color = if (formState.alertType == type)
                                Theme.colors.textColors.bodyColor
                            else
                                Theme.colors.textColors.hintColor
                        )
                    }
                }
            }

            Spacer(Modifier.height(Theme.spacing.large))

            // ── Save button ────────────────────────────────────────────────────
            Button(
                onClick  = onSave,
                enabled  = formState.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = Theme.colors.primary,
                    contentColor           = Theme.colors.onBodyColor,
                    disabledContainerColor = Theme.colors.primary.copy(alpha = 0.38f),
                    disabledContentColor   = Theme.colors.onBodyColor.copy(alpha = 0.38f)
                ),
                shape = Theme.shapes.medium
            ) {
                if (formState.isSaving) {
                    CircularProgressIndicator(
                        color       = Theme.colors.onBodyColor,
                        strokeWidth = 2.dp,
                        modifier    = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text  = stringResource(R.string.save_alert),
                        style = Theme.typography.bodyMedium
                    )
                }
            }
        }
    }
}