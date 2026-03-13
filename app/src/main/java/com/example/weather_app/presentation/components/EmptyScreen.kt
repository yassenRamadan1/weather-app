package com.example.weather_app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun EmptyScreen(
    message: String,
    painter: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(painter),
            contentDescription = null,
            modifier = Modifier.padding(bottom = Theme.spacing.large)
        )
        Text(
            text = message,
            style = Theme.typography.bodyLarge,
            color = Theme.colors.textColors.bodyColor
        )
    }
}