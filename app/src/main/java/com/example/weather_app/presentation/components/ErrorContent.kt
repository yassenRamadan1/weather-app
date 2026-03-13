package com.example.weather_app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weather_app.R
import com.example.weather_app.designsystem.components.WTButton
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.presentation.uierror.UiText

@Composable
fun ErrorContent(message: UiText, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.something_went_wrong),
            style = Theme.typography.bodyLarge
        )
        Text(
            message.asString(),
            textAlign = TextAlign.Center,
            color = Theme.colors.errorColor
        )
        WTButton(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.retry)
        )
    }
}