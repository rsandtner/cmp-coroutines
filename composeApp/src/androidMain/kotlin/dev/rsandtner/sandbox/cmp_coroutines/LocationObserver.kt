package dev.rsandtner.sandbox.cmp_coroutines

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class LocationObserver(
    private val context: Context
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // permissions are checked in hasPermissions
    fun observeLocation(interval: Duration) = callbackFlow @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION]) {
        val locationManager = context.getSystemService<LocationManager>()!!

        var gpsEnabled = false
        var networkEnabled = false

        while (!gpsEnabled && !networkEnabled) {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!gpsEnabled && !networkEnabled) {
                delay(3.seconds)
            }
        }

        if (!hasPermissions(context)) {
            close()
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval.inWholeMilliseconds)
            .build()

        val callback = object: LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        client.requestLocationUpdates(request, callback, context.mainLooper)
        awaitClose { client.removeLocationUpdates(callback) }

    }

    private fun hasPermissions(context: Context) = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        .all {
            checkSelfPermission(
                context,
                it
            ) == PERMISSION_GRANTED
        }

}
