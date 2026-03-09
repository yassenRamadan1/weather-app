package com.example.weather_app.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun FavoriteDetailsScreen(
    lat: Double,
    lon: Double,
    onNavigateBack: () -> Unit
) {
        FavoriteDetailsScreenContent(
            lat = lat,
            lon = lon,
            onNavigateBack
        )
}
@Composable
fun FavoriteDetailsScreenContent(
    lat: Double,
    lon: Double,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().padding(
            WindowInsets.systemBars.asPaddingValues()
        )
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Theme.colors.gradientBackground.gradientBackgroundStart,
                        Theme.colors.gradientBackground.gradientBackgroundEnd
                    )
                )
        ),
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) { Text(
                text = "Favorite Details",
                modifier = Modifier.padding(16.dp)
            )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.padding(16.dp).clickable { onNavigateBack() }
                )
            }


        }
    ) {it
        Text(
            text = "Favorite Details Screen\nLatitude: $lat\nLongitude: $lon"
        )
    }

}