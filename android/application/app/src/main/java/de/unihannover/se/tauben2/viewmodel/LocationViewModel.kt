package de.unihannover.se.tauben2.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

@SuppressLint("MissingPermission")
class LocationViewModel(context: Context): ViewModel(), LocationListener {

    val currentLocation: MutableLiveData<Location?> = MutableLiveData()

    init {
        // TODO make external Singleton
        (context.getSystemService(LOCATION_SERVICE) as LocationManager)
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 300,
                50F, this)
    }

    override fun onLocationChanged(location: Location?) {
        currentLocation.postValue(location)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}
}
