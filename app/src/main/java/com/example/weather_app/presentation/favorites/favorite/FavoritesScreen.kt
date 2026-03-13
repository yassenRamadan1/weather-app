package com.example.weather_app.presentation.favorites.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_app.R
import com.example.weather_app.designsystem.components.WTFloatingButton
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.designsystem.theme.WTTheme
import com.example.weather_app.domain.entity.weather.FavoriteLocation
import com.example.weather_app.presentation.components.EmptyScreen
import com.example.weather_app.presentation.components.ErrorContent
import com.example.weather_app.presentation.components.LocationPickerScreen
import com.example.weather_app.presentation.favorites.components.FavoriteWeatherCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoriteScreenViewModel = koinViewModel(),
    onClickFavorite: (lat: Double, lon: Double) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showMapPicker by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Theme.colors.gradientBackground.gradientBackgroundStart,
            Theme.colors.gradientBackground.gradientBackgroundEnd
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = backgroundGradient
            )
    ) {
        when (val state = uiState) {
            is FavoritesScreenUiState.Empty -> {
                EmptyScreen(
                    message = stringResource(R.string.no_favorite_locations_yet_add_some_to_see_them_here),
                    painter = R.drawable.no_favorite_locations
                )
            }

            is FavoritesScreenUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = { viewModel.loadFavoriteLocations() })

            FavoritesScreenUiState.Loading -> {
                CircularProgressIndicator(
                    color = Theme.colors.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is FavoritesScreenUiState.Success -> FavoritesScreenContent(
                onClickFavoriteItem = { lat, lon -> onClickFavorite(lat, lon) },
                onDeleteLocation = { lat, lon -> viewModel.removeFavoriteLocation(lat, lon) },
                favoriteLocations = state.favoriteLocations
            )
        }
        WTFloatingButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Theme.spacing.large),
            onClick = { showMapPicker = true },
        )
        if (showMapPicker) {
            LocationPickerScreen(
                initialLat = 30.0444,
                initialLon = 31.2357,
                onLocationSelected = { picked ->
                    showMapPicker = false
                    viewModel.addFavoriteLocation(
                        picked.lat,
                        picked.lon,
                        picked.cityName ?: "Unknown"
                    )
                },
                onDismiss = { showMapPicker = false },
            )
        }

    }
}

@Composable
fun FavoritesScreenContent(
    onClickFavoriteItem: (lat: Double, lon: Double) -> Unit,
    onDeleteLocation: (lat: Double, lon: Double) -> Unit,
    favoriteLocations: List<FavoriteLocation>,
) {
    var locationToDelete by remember { mutableStateOf<FavoriteLocation?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Theme.spacing.medium,
                end = Theme.spacing.medium,
                top = Theme.spacing.extraLarge,
            )
    ) {
        items(favoriteLocations.size) { index ->
            val location = favoriteLocations[index]
            FavoriteWeatherCard(
                location = location.cityName,
                onClickRemove = {
                    locationToDelete = location
                    showDeleteDialog = true
                },
                weatherIconRes = R.drawable.img_cloud,
                modifier = Modifier
                    .padding(bottom = Theme.spacing.medium)
                    .clickable { onClickFavoriteItem(location.lat, location.lon) }
            )
        }
    }
    if (showDeleteDialog && locationToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                locationToDelete = null
            },
            title = {
                Text(text = stringResource(R.string.remove_location),
                    style = Theme.typography.title,
                    color = Theme.colors.textColors.titleColor
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.remove_location_confirmation,
                        locationToDelete?.cityName ?: ""
                    ),
                    style = Theme.typography.bodyMedium,
                    color = Theme.colors.textColors.bodyColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        locationToDelete?.let {
                            onDeleteLocation(it.lat, it.lon)
                        }
                        showDeleteDialog = false
                        locationToDelete = null
                    }
                ) {
                    Text(
                        text =
                            stringResource(R.string.remove),
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.errorColor
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        locationToDelete = null
                    }
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.textColors.bodyColor
                    )
                }
            },
            containerColor = Theme.colors.gradientBackground.gradientBackgroundEnd
        )
    }
}


@Preview
@Composable
fun FavoritesScreenPreview() {
    FavoritesScreenContent(
        onClickFavoriteItem = { _, _ -> },
        onDeleteLocation = { _, _ -> },
        favoriteLocations = listOf(
            FavoriteLocation("asuncion", "paraguay", 1.0, 1.0),
            FavoriteLocation("potosi", "bolivia", 1.0, 1.0),
            FavoriteLocation("lima", "peru", 1.0, 1.0),
            FavoriteLocation("lima", "peru", 1.0, 1.0),
            FavoriteLocation("lima", "peru", 1.0, 1.0),
            FavoriteLocation("lima", "peru", 1.0, 1.0),
            FavoriteLocation("lima", "peru", 1.0, 1.0),
        )
    )
}

@Preview
@Composable
fun FavoritesScreenLightPreview() {
    WTTheme(isDarkTheme = false) {
        FavoritesScreenContent(
            onClickFavoriteItem = { _, _ -> },
            onDeleteLocation = { _, _ -> },
            favoriteLocations = listOf(
                FavoriteLocation("asuncion", "paraguay", 1.0, 1.0),
                FavoriteLocation("potosi", "bolivia", 1.0, 1.0),
                FavoriteLocation("lima", "peru", 1.0, 1.0),
                FavoriteLocation("lima", "peru", 1.0, 1.0),
                FavoriteLocation("lima", "peru", 1.0, 1.0),
                FavoriteLocation("lima", "peru", 1.0, 1.0),
                FavoriteLocation("lima", "peru", 1.0, 1.0),
            )
        )
    }
}
