package de.unihannover.se.tauben2.model

import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.model.database.entity.Case

interface MapMarkable {
    fun getMarker() : MarkerOptions
}