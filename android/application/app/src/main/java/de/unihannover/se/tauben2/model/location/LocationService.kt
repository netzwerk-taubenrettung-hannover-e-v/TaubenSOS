package de.unihannover.se.tauben2.model.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationService(context: Context): LocationCallback() {

    private val listeners = mutableSetOf<LocationChangedListener>()

    private var mFusedLocProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    init {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest().apply {
                interval = 10000
                fastestInterval = 8000
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(context)
            // TODO check permissions availability with task
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                mFusedLocProvider.requestLocationUpdates(locationRequest, this, null)
            }
        }
    }

    override fun onLocationResult(result: LocationResult?) {
        result?.lastLocation?.let { loc ->
            listeners.forEach { it.onLocationChanged(loc) }
        }
        super.onLocationResult(result)
    }

    companion object {

        var inst : LocationService? = null

        fun getInstance(context: Context): LocationService {
            if(inst == null)
                inst = LocationService(context)
            return inst!!
        }
    }

    fun registerWithLastKnownLocation(context: Context, listener: LocationChangedListener) {
        register(listener)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocProvider.lastLocation.addOnSuccessListener {
                listener.onLocationChanged(it)
            }
        }
    }

    fun register(listener: LocationChangedListener) {
        listeners.add(listener)
    }

    fun unregister(listener: LocationChangedListener) {
        listeners.remove(listener)
    }
}

interface LocationChangedListener {
    fun onLocationChanged(location: Location)
}