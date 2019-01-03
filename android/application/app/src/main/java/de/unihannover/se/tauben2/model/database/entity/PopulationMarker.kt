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
@Entity(tableName = "population", primaryKeys = ["latitude", "longitude", "populationMarkerID"])
data class PopulationMarker(val latitude: Double,
                            val longitude: Double,
                            var description: String,
                            val populationMarkerID: Int,
                            var radius: Double,
                            var values: List<CounterValue>
) : MapMarkable, DatabaseEntity() {

    override val refreshCooldown: Long
        get() = 1000 * 60 * 60 * 24 // 24 hours

    override fun getMarker(): MarkerOptions {
        val totalPigeonCount = values.fold(0) { sum, element -> sum + element.pigeonCount }
        return MarkerOptions().position(LatLng(latitude, longitude))
                .title(description)
                .snippet("Taubenanzahl: $totalPigeonCount")
    }

    companion object: AllUpdatable {
        override val refreshAllCooldown: Long
            get() = 1000 * 60 * 15 // 30 min

    }
}
