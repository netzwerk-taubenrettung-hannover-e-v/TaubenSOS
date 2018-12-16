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

    private var mLocationRequest: LocationRequest? = null

    private var mFusedLocProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private var mTask: Task<LocationSettingsResponse>? = null

    init {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationRequest = LocationRequest().apply {
                interval = 10000
                fastestInterval = 8000
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }

            mLocationRequest?.let { locationRequest ->

                val builder = LocationSettingsRequest.Builder()
                builder.addLocationRequest(locationRequest)

                val client: SettingsClient = LocationServices.getSettingsClient(context)
                // TODO check permissions availability with task
                mTask = client.checkLocationSettings(builder.build())
                mTask?.addOnSuccessListener {
                    mFusedLocProvider.requestLocationUpdates(locationRequest, this, null)
                }
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
        register(context, listener)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocProvider.lastLocation.addOnSuccessListener {
                if(it!=null) {
                    listener.onLocationChanged(it)
                }
            }
        }
    }

    fun register(context: Context, listener: LocationChangedListener) {
        listeners.add(listener)
        if(listeners.size == 1 && mLocationRequest != null &&
                ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))) {

            mTask?.addOnSuccessListener {
                mFusedLocProvider.requestLocationUpdates(mLocationRequest, this, null)
            }
        }
    }

    fun unregister(listener: LocationChangedListener) {
        listeners.remove(listener)
        if(listeners.isEmpty())
            mFusedLocProvider.removeLocationUpdates(this)
    }
}

interface LocationChangedListener {
    fun onLocationChanged(location: Location)
}