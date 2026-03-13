package com.example.weather_app.presentation.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun PreviewComponentsBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val brushBackground = Brush.linearGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd,
        )
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing.large)
            .background(brush = brushBackground),
        contentAlignment = Alignment.Center
    ){
        content()
    }

}