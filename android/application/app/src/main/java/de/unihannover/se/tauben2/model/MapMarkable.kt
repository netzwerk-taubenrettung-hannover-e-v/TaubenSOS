package de.unihannover.se.tauben2.model

import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.model.entity.Case

interface MapMarkable {
    fun getMarker() : MarkerOptions
    fun getMarkerCase() : Case
}