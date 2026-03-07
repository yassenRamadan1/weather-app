package com.example.weather_app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun SettingsScreen() {
    SettingsContent()
}

@Composable
fun SettingsContent() {
    val brush = Brush.verticalGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd
        )
    )
    Box(
        modifier = Modifier.fillMaxSize().background(brush = brush).padding(WindowInsets.systemBars.asPaddingValues())
    )   {

        Column(
            modifier = Modifier.fillMaxWidth().padding(Theme.spacing.large)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dark Mode", color = Color.White)
                Switch(checked = false, onCheckedChange = { /* TODO */ })
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Notifications", color = Color.White)
                Switch(checked = true, onCheckedChange = { /* TODO */ })
            }
        }

    }
}