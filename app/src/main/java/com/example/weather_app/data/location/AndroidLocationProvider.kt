package com.example.weather_app.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import com.example.weather_app.domain.repository.LocationProvider

class AndroidLocationProvider(
    private val context: Context,
    private val fusedClient: FusedLocationProviderClient
) : LocationProvider {
    override fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    override fun isLocationServicesEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }
    override suspend fun getCurrentLocationOrLastKnown(): Location? {
        if (!hasPermission()) return null

        val fresh = withTimeoutOrNull(10_000L) { tryGetCurrentLocation() }
        if (fresh != null) return fresh
        return tryGetLastLocation()
    }
    private suspend fun tryGetCurrentLocation(): Location? {
        return try {
            suspendCancellableCoroutine { cont ->
                val cts = com.google.android.gms.tasks.CancellationTokenSource()
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setDurationMillis(8_000L)
                    .setMaxUpdateAgeMillis(30_000L)
                    .build()

                fusedClient.getCurrentLocation(request, cts.token)
                    .addOnSuccessListener { loc -> if (cont.isActive) cont.resume(loc) }
                    .addOnFailureListener { e -> if (cont.isActive) cont.resumeWithException(e) }

                cont.invokeOnCancellation { cts.cancel() }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }
    override suspend fun tryGetLastLocation(): Location? {
        if (!hasPermission()) return null
        return try {
            suspendCancellableCoroutine { cont ->
                fusedClient.lastLocation
                    .addOnSuccessListener { loc -> if (cont.isActive) cont.resume(loc) }
                    .addOnFailureListener { if (cont.isActive) cont.resume(null) }
                    .addOnCanceledListener { if (cont.isActive) cont.resume(null) }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }
}