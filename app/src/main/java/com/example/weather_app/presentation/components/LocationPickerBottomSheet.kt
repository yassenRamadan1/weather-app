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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.weather_app.R
import com.example.weather_app.designsystem.theme.Theme
import com.example.weather_app.presentation.locationpicker.model.AddressDetails
import com.example.weather_app.presentation.locationpicker.model.PickedLocation

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

@OptIn( FlowPreview::class)
@Composable
fun LocationPickerScreen(
    onLocationSelected: (PickedLocation) -> Unit,
    onDismiss: () -> Unit,
    initialLat: Double = 31.0822,
    initialLon: Double = 29.7408,
    isDarkTheme: Boolean = Theme.colors.isDark,
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

    val locationNotFound = stringResource(R.string.location_not_found)
    val unknownLocation = stringResource(R.string.unknown_location)

    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(800L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() && it.length >= 3 }
            .collectLatest { query ->
                isSearching = true
                searchError = null
                val result = geocodeCity(context, query)

                if (result != null) {
                    pickedLocation = result
                    cameraState.animateTo(
                        CameraPosition(target = Position(result.lon, result.lat), zoom = 13.0)
                    )
                } else {
                    searchError = locationNotFound
                }
                isSearching = false
            }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                baseStyle = BaseStyle.Uri(
                    if (isDarkTheme) "https://tiles.openfreemap.org/styles/dark"
                    else "https://tiles.openfreemap.org/styles/liberty"
                ),
                cameraState = cameraState,
                options = MapOptions(
                    gestureOptions = GestureOptions(
                        isScrollEnabled = true,
                        isZoomEnabled = true,
                        isTiltEnabled = true,
                        isRotateEnabled = true
                    )
                ),
                onMapClick = { point, _ ->
                    keyboard?.hide()
                    scope.launch {
                        val details = reverseGeocodeLatLng(context, point.latitude, point.longitude)
                        val city = details?.city ?: unknownLocation
                        val countryCode = details?.countryCode ?: ""
                        pickedLocation = PickedLocation(
                            lat = point.latitude,
                            lon = point.longitude,
                            cityName = city,
                            countryCode = countryCode
                        )
                    }
                    ClickResult.Consume
                }
            ) {
                pickedLocation?.let { loc ->
                    val markerSource = rememberGeoJsonSource(
                        data = GeoJsonData.Features(
                            FeatureCollection(Feature(geometry = Point(Position(loc.lon, loc.lat))))
                        )
                    )
                    SymbolLayer(
                        id = "picked-pin-layer",
                        source = markerSource,
                        iconImage = image(painterResource(R.drawable.location_anchor), drawAsSdf = true),
                        iconColor = const(Theme.colors.buttonColor),
                        iconSize = const(2.5f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Theme.colors.gradientBackground.gradientBackgroundEnd.copy(alpha = 0.9f))
            ) {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { 
                            Text(
                                text = stringResource(R.string.search_location_placeholder),
                                style = Theme.typography.bodyMedium,
                                color = Theme.colors.textColors.hintColor
                            ) 
                        },
                        leadingIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                    contentDescription = stringResource(R.string.back),
                                    tint = Theme.colors.textColors.titleColor
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    searchError = null
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear, 
                                        contentDescription = stringResource(R.string.clear_search),
                                        tint = Theme.colors.textColors.titleColor
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = Theme.typography.bodyLarge.copy(color = Theme.colors.textColors.titleColor),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            cursorColor = Theme.colors.primary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() })
                    )

                    if (isSearching) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Theme.colors.primary,
                            trackColor = Theme.colors.primary.copy(alpha = 0.2f)
                        )
                    }
                    searchError?.let {
                        Text(
                            text = it,
                            color = Theme.colors.errorColor,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = Theme.typography.hint
                        )
                    }
                }
            }

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
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Theme.colors.gradientBackground.gradientBackgroundEnd.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.selected_location),
                            style = Theme.typography.hint,
                            color = Theme.colors.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pickedLocation?.cityName ?: stringResource(R.string.custom_coordinates),
                            style = Theme.typography.title,
                            color = Theme.colors.textColors.titleColor
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { pickedLocation?.let { onLocationSelected(it) } },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.buttonColor)
                        ) {
                            Text(
                                text = stringResource(R.string.confirm_location),
                                style = Theme.typography.bodyMedium,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun geocodeCity(context: android.content.Context, query: String): PickedLocation? =
    withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { cont ->
                    geocoder.getFromLocationName(query, 1) { addresses ->
                        val first = addresses.firstOrNull()
                        if (first != null) {
                            val city = first.locality ?: first.subAdminArea ?: query
                            val countryCode = first.countryCode ?: ""
                            cont.resume(PickedLocation(first.latitude, first.longitude, city, countryCode))
                        } else {
                            cont.resume(null)
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 1)
                val first = addresses?.firstOrNull()
                if (first != null) {
                    val city = first.locality ?: first.subAdminArea ?: query
                    val countryCode = first.countryCode ?: ""
                    PickedLocation(first.latitude, first.longitude, city, countryCode)
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

private suspend fun reverseGeocodeLatLng(
    context: android.content.Context,
    lat: Double,
    lon: Double
): AddressDetails? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCoroutine { cont ->
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    if (address != null) {
                        val city = address.locality ?: address.subAdminArea ?: "Unknown Location"
                        val countryCode = address.countryCode ?: ""
                        cont.resume(AddressDetails(city, countryCode))
                    } else {
                        cont.resume(null)
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val address = geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()
            if (address != null) {
                val city = address.locality ?: address.subAdminArea ?: "Unknown Location"
                val countryCode = address.countryCode ?: ""
                AddressDetails(city, countryCode)
            } else null
        }
    } catch (_: Exception) {
        null
    }
}