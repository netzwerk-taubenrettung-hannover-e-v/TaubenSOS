package de.unihannover.se.tauben2.model.database.entity

import androidx.room.Entity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.model.MapMarkable


/**
 * Entity for storing count data of pigeons
 * @param latitude latitude coordinate
 * @param longitude longitude coordinate
 * @param timestamp unix timestamp when the pigeons where counted
 */
@Entity(tableName = "population", primaryKeys = ["latitude", "longitude"])
data class PigeonCounter(var latitude: Double,
                         var longitude: Double,
                         var description: String,
                         var populationMarkerID: Long,
                         var radius: Double
) : MapMarkable {

    override fun getMarker(): MarkerOptions {
        return MarkerOptions().position(LatLng(latitude, longitude)).title("Taubenanzahl: xy")
    }
}