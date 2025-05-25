package com.example.sendmessagewhatsapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class Ubicacion(private val activity: Activity) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestCode
        )
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationResult: (Location?) -> Unit) {
        if (!checkLocationPermission()) {
            onLocationResult(null)
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)//Hacemos un request de la ubicación cada cierto tiempo
            .setMinUpdateIntervalMillis(5_000L)
            .setMaxUpdates(1)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                onLocationResult(location)
                // Una vez que tenemos la ubicación, dejamos de recibir actualizaciones
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}
