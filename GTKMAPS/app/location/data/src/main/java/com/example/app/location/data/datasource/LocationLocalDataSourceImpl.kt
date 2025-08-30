package com.example.app.location.data.datasource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.example.app.location.domain.datasource.LocationLocalDataSource
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

/** [com.example.travel_mate.LocationLocalDataSource]
 * Legacy class to receive location updates
 */
class LocationLocalDataSourceImpl(private val context: Context): LocationLocalDataSource {

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun getCurrentLocation(): Location? {
        val client = LocationServices.getFusedLocationProviderClient(context)

        val location = if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await();
        } else {
            null
        }

        return location
    }

    private var latestLocation: Location? = null
    private var locationCallback: LocationCallback? = null

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun startContinuousLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000
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

    override fun stopLocationUpdates() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback?.let {
            fusedClient.removeLocationUpdates(it)
        }
    }

    override suspend fun updateCurrentLocation(): Location? = latestLocation
}