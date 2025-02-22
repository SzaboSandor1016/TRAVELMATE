package com.example.gtk_maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

class ClassLocationListener(private val context: Context) : LocationListener {
    private val currentLocation: Location?
    private var latitude = 0.0
    private var longitude = 0.0

    protected var locationManager: LocationManager? = null

    init {
        this.currentLocation = this.location
    }

    private val location: Location?
        get() {
            var currentLocation: Location? = null

            locationManager = context
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    2
                )
            }
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1,
                0.1f,
                this
            )
            if (locationManager != null) {
                val location = locationManager!!
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    currentLocation = Location(location)
                }
            }
            return currentLocation
        }

    fun getLongitude(): Double {
        if (currentLocation != null) {
            longitude = currentLocation.longitude
        }
        return longitude
    }

    fun getLatitude(): Double {
        if (currentLocation != null) {
            latitude = currentLocation.latitude
        }
        return latitude
    }

    fun stopListener() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager!!.removeUpdates(this@ClassLocationListener)
        }
    }

    override fun onLocationChanged(location: Location) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }
}