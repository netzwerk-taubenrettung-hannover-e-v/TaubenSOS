package de.unihannover.se.tauben2.model.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat

class LocationService(context: Context): LocationListener {

    private val listeners = mutableSetOf<LocationChangedListener>()

    init {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                            200F, this)
        }
    }

    companion object {

        var inst : LocationService? = null

        fun getInstance(context: Context): LocationService {
            if(inst == null)
                inst = LocationService(context)
            return inst!!
        }
    }

    fun register(listener: LocationChangedListener) {
        listeners.add(listener)
    }

    fun unregister(listener: LocationChangedListener) {
        listeners.remove(listener)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}

    override fun onLocationChanged(location: Location?) {
        location?.let { loc ->
            listeners.forEach { it.onLocationChanged(loc) }
        }
    }
}

interface LocationChangedListener {
    fun onLocationChanged(location: Location)
}