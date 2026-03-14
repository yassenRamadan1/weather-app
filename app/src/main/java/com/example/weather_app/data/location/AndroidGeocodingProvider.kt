package com.example.weather_app.data.location

import android.location.Geocoder
import android.os.Build
import com.example.weather_app.domain.repository.GeocodingProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidGeocodingProvider(
    private val geocoder: Geocoder
) : GeocodingProvider {
    override suspend fun reverseGeocode(lat: Double, lon: Double): String? =
        withContext(Dispatchers.IO) {
            try {
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
}
