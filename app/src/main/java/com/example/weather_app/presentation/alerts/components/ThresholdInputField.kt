package com.example.weather_app.presentation.alerts.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun ThresholdInputField(
    label: String,
    value: String,
    suffix: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    allowNegative: Boolean = true,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { raw ->
                val filtered = if (allowNegative) {
                    raw.filter { it.isDigit() || it == '.' || it == '-' }
                        .let { s ->
                            if (s.count { it == '-' } > 1) s.replace("-", "").let { "-$it" }
                            else s
                        }
                } else {
                    raw.filter { it.isDigit() || it == '.' }
                }
                onValueChange(filtered)
            },
            label = {
                Text(
                    text = label,
                    style = Theme.typography.bodySmall,
                    color = Theme.colors.textColors.hintColor
                )
            },
            trailingIcon = {
                Text(
                    text = suffix,
                    style = Theme.typography.bodyMedium,
                    color = Theme.colors.textColors.hintColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (allowNegative) KeyboardType.Number else KeyboardType.Decimal
            ),
            textStyle = Theme.typography.bodyMedium.copy(
                color = Theme.colors.textColors.bodyColor
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Theme.colors.primary,
                unfocusedBorderColor = Theme.colors.textColors.hintColor,
                cursorColor = Theme.colors.primary,
                focusedLabelColor = Theme.colors.primary,
                unfocusedLabelColor = Theme.colors.textColors.hintColor,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}