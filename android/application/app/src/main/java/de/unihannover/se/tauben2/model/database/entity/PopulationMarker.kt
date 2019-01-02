package de.unihannover.se.tauben2.model.database.entity

import androidx.room.Entity
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.database.converter.CounterValueConverter


/**
 * Entity for storing count data of pigeons
 * @param latitude latitude coordinate
 * @param longitude longitude coordinate
 * @param values list of CounterValue instances at the given Marker
 */
@TypeConverters(CounterValueConverter::class)
@Entity(tableName = "population", primaryKeys = ["latitude", "longitude"])
data class PopulationMarker(var latitude: Double,
                            var longitude: Double,
                            var description: String,
                            var populationMarkerID: Long,
                            var radius: Double,
                            var values: List<CounterValue>
) : MapMarkable {

    override fun getMarker(): MarkerOptions {
        val totalPigeonCount = values.fold(0) { sum, element -> sum + element.pigeonCount }
        return MarkerOptions().position(LatLng(latitude, longitude))
                .title(description)
                .snippet("Taubenanzahl: $totalPigeonCount")
    }
}
