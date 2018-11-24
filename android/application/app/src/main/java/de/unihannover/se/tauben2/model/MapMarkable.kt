package de.unihannover.se.tauben2.model

import com.google.android.gms.maps.model.MarkerOptions

interface MapMarkable {
    fun getMarker() : MarkerOptions
}