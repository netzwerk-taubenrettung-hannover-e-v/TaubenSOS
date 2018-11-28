package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import de.unihannover.se.tauben2.model.location.LocationChangedListener
import de.unihannover.se.tauben2.model.location.LocationService

class LocationViewModel(context: Context): ViewModel(), LocationChangedListener {

    private val currentLocation: MutableLiveData<Location?> = MutableLiveData()
    private val locationService: LocationService = LocationService.getInstance(context)

    override fun onLocationChanged(location: Location) {
        currentLocation.postValue(location)
    }

    fun observeCurrentLocation(owner: LifecycleOwner, observer: Observer<in Location?>) {
        currentLocation.observe(owner, observer)
        locationService.register(this)
    }

    fun stopObservingCurrentLocation (observer: Observer<in Location?>) {
        currentLocation.removeObserver(observer)
        if(!currentLocation.hasActiveObservers())
            locationService.unregister(this)
    }
}
