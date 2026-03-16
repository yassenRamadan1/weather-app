package com.example.weather_app.presentation.locationpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.weather_app.presentation.locationpicker.model.PickedLocation
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@Composable
internal fun LocationMap(
    modifier: Modifier,
    mapConfig: MapConfig,
    cameraState: CameraState,
    pickedLocation: PickedLocation?,
    markerPainter: Painter,
    markerIconSize: Float,
    markerColor: Color = Color.Unspecified,
    isDarkTheme: Boolean = false,
    onMapClick: (lat: Double, lon: Double) -> Unit,
) {
    MaplibreMap(
        modifier = modifier,
        baseStyle = BaseStyle.Uri(mapConfig.getStyleUri(isDarkTheme)),
        cameraState = cameraState,
        options = MapOptions(gestureOptions = mapConfig.gestureOptions),
        onMapClick = { point, _ ->
            onMapClick(point.latitude, point.longitude)
            ClickResult.Consume
        },
    ) {
        pickedLocation?.let { loc ->
            val markerSource = rememberGeoJsonSource(
                data = GeoJsonData.Features(
                    FeatureCollection(
                        Feature(geometry = Point(Position(loc.lon, loc.lat)))
                    )
                )
            )
            SymbolLayer(
                id = "picked-pin-layer",
                source = markerSource,
                iconImage = image(markerPainter, drawAsSdf = true),
                iconColor = const(if (markerColor == Color.Unspecified) MaterialTheme.colorScheme.primary else markerColor),
                iconSize = const(markerIconSize),
            )
        }
    }
}

@Composable
internal fun LocationSearchBar(
    modifier: Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onDismiss: () -> Unit,
    isSearching: Boolean,
    searchError: String?,
    placeholder: String,
    shape: Shape,
    elevation: Dp,
    colors: SearchBarColors,
    leadingIcon: @Composable (() -> Unit)?,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (colors.containerColor == Color.Unspecified)
                MaterialTheme.colorScheme.surface
            else colors.containerColor
        ),
    ) {
        Column {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                leadingIcon = leadingIcon ?: {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.error,
                    unfocusedBorderColor =MaterialTheme.colorScheme.error,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        defaultKeyboardAction(ImeAction.Search)
                    }
                ),
            )

            if (isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            searchError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
internal fun DefaultConfirmationCard(
    pickedLocation: PickedLocation,
    colors: ConfirmationCardColors,
    shape: Shape,
    elevation: Dp,
    onConfirm: () -> Unit,
) {
    val labelColor = if (colors.labelColor == Color.Unspecified)
        MaterialTheme.colorScheme.primary else colors.labelColor
    val titleColor = if (colors.titleColor == Color.Unspecified)
        MaterialTheme.colorScheme.onSurface else colors.titleColor

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (colors.containerColor == Color.Unspecified)
                MaterialTheme.colorScheme.surface
            else colors.containerColor
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Selected Location",
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = pickedLocation.cityName ?: "Custom Coordinates",
                style = MaterialTheme.typography.titleMedium,
                color = titleColor,
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = if (colors.buttonContainerColor != Color.Unspecified)
                    ButtonDefaults.buttonColors(containerColor = colors.buttonContainerColor)
                else ButtonDefaults.buttonColors(),
            ) {
                Text("Confirm Location")
            }
        }
    }
}
