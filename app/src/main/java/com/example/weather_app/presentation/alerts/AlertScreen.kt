package com.example.weather_app.presentation.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun AlertScreen() {
        AlertScreenContent()
}
@Composable
fun AlertScreenContent() {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd
        )
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient),
        contentWindowInsets = WindowInsets.systemBars,
        floatingActionButton = {
            Button(
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colors.primary),
                shape = Theme.shapes.circle
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Favorite",
                    tint = Theme.colors.primary
                )
            }
        }
    ) {

    }
}