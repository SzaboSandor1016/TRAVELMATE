package com.example.travel_mate

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** [com.example.travel_mate.LocationLocalDataSource]
 * Legacy class to receive location updates
 */
class LocationLocalDataSource(private val context: Context){

    suspend fun getCurrentLocation(): Location? {
        val client = LocationServices.getFusedLocationProviderClient(context)

        val location = if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
        } else {
            null
        }

        return location
    }

    private var latestLocation: Location? = null
    private var locationCallback: LocationCallback? = null

    fun startContinuousLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000 // akár 1 mp-enként kérhet friss adatot
            ).apply {
                setMinUpdateIntervalMillis(1000)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    latestLocation = result.lastLocation
                }
            }

            fusedClient.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())
        }
    }

    fun stopLocationUpdates() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback?.let {
            fusedClient.removeLocationUpdates(it)
        }
    }

    suspend fun updateCurrentLocation(): Location? = latestLocation
}