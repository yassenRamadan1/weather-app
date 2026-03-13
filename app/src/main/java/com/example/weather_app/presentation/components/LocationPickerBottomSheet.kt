package com.example.weather_app.presentation.components

import android.location.Geocoder
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.presentation.components.models.PickedLocation
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun LocationPickerScreen(
    onLocationSelected: (PickedLocation) -> Unit,
    onDismiss: () -> Unit,
    initialLat: Double = 31.0822,
    initialLon: Double = 29.7408,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    val cameraState = rememberCameraState(
        CameraPosition(target = Position(initialLon, initialLat), zoom = 12.0)
    )

    var searchQuery by remember { mutableStateOf("") }
    var pickedLocation by remember { mutableStateOf<PickedLocation?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }

    // --- Search As You Type (Debounce) Logic ---
    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(800L) // Wait 800ms after user stops typing
            .distinctUntilChanged()
            .filter { it.isNotBlank() && it.length >= 3 }
            .collectLatest { query ->
                isSearching = true
                searchError = null
                val result = geocodeCity(context, query)

                if (result != null) {
                    pickedLocation = result
                    // Animate camera smoothly to the searched city
                    cameraState.animateTo(
                        CameraPosition(target = Position(result.lon, result.lat), zoom = 13.0)
                    )
                } else {
                    searchError = "Location not found."
                }
                isSearching = false
            }
    }

    // Using a Full-Screen Dialog completely fixes gesture collisions
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false // Allows drawing edge-to-edge
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. --- The Map (Background) ---
            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
                cameraState = cameraState,
                // Explicitly unlock all gestures for full world navigation
                options = MapOptions(
                    gestureOptions = GestureOptions(
                        isScrollEnabled = true,
                        isZoomEnabled = true,
                        isTiltEnabled = true,
                        isRotateEnabled = true
                    )
                ),
                onMapClick = { point, _ ->
                    keyboard?.hide() // UX: Hide keyboard when touching map
                    scope.launch {
                        val city = reverseGeocodeLatLng(context, point.latitude, point.longitude)
                        pickedLocation = PickedLocation(point.latitude, point.longitude, city)
                    }
                    ClickResult.Consume
                }
            ) {
                // Pin Marker Layer
                pickedLocation?.let { loc ->
                    val markerSource = rememberGeoJsonSource(
                        data = GeoJsonData.Features(
                            FeatureCollection(Feature(geometry = Point(Position(loc.lon, loc.lat))))
                        )
                    )
                    SymbolLayer(
                        id = "picked-pin-layer",
                        source = markerSource,
                        // Make sure ic_launcher_foreground or your pin icon is in drawable
                        iconImage = image(painterResource(R.drawable.location), drawAsSdf = true),
                        iconColor = const(Theme.colors.buttonColor),
                        iconSize = const(2.5f) // Slightly larger for better visibility
                    )
                }
            }

            // 2. --- Floating Search Bar (Top) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search location...") },
                        leadingIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    searchError = null
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() })
                    )

                    if (isSearching) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    searchError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 3. --- Floating Confirmation Card (Bottom) ---
            AnimatedVisibility(
                visible = pickedLocation != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp)
                    .padding(horizontal = 16.dp),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = "Selected Location",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pickedLocation?.cityName ?: "Custom Coordinates",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { pickedLocation?.let { onLocationSelected(it) } },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.buttonColor)
                        ) {
                            Text("Confirm Location")
                        }
                    }
                }
            }
        }
    }
}

// Helper functions remain unchanged
private suspend fun geocodeCity(context: android.content.Context, query: String): PickedLocation? =
    withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { cont ->
                    geocoder.getFromLocationName(query, 1) { addresses ->
                        val first = addresses.firstOrNull()
                        cont.resume(
                            if (first != null)
                                PickedLocation(first.latitude, first.longitude, first.locality ?: query)
                            else null
                        )
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 1)
                val first = addresses?.firstOrNull()
                if (first != null)
                    PickedLocation(first.latitude, first.longitude, first.locality ?: query)
                else null
            }
        } catch (e: Exception) {
            null
        }
    }

private suspend fun reverseGeocodeLatLng(
    context: android.content.Context,
    lat: Double,
    lon: Double
): String? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCoroutine { cont ->
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    cont.resume(addresses.firstOrNull()?.locality)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.locality
        }
    } catch (e: Exception) {
        null
    }
}