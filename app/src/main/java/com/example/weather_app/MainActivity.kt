package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.designsystem.theme.WTTheme
import com.example.weather_app.designsystem.theme.WeatherappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WTTheme {
                ShowWeatherScreen()
            }
        }
    }
}

@Composable
fun ShowWeatherScreen() {
    val backgroundGradientBrush = Brush.verticalGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradientBrush)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.systemBars,
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Text(
                    text = "Hello, Weather App!//مرحبا، تطبيق الطقس!",
                    color = Theme.colors.textColors.titleColor,
                    style = Theme.typography.title,
                )
            }

        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    WTTheme(false, isArabic = true
    ) {
        ShowWeatherScreen()
    }
}