package de.unihannover.se.tauben2.model.database.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.database.converter.CounterValueConverter
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


/**
 * Entity for storing count data of pigeons
 * @param latitude latitude coordinate
 * @param longitude longitude coordinate
 * @param values list of CounterValue instances at the given Marker
 */
@Parcelize
@TypeConverters(CounterValueConverter::class)
@Entity(tableName = "population")
data class PopulationMarker(val latitude: Double,
                            val longitude: Double,
                            var description: String,
                            @PrimaryKey val populationMarkerID: Int,
                            var radius: Double,

                            var values: List<CounterValue>
) : MapMarkable, DatabaseEntity(), Parcelable {

    override val refreshCooldown: Long
        get() = 1000 * 60 * 60 * 24 // 24 hours

    override fun getMarker(): MarkerOptions {
        val totalPigeonCount = values.fold(0) { sum, element -> sum + element.pigeonCount }
        return MarkerOptions().position(LatLng(latitude, longitude))
                .title("Taubenanzahl: $totalPigeonCount")
    }

    companion object: AllUpdatable {
        override val refreshAllCooldown: Long
            get() = 0 //1000 * 60 * 15 // 30 min

    }
}
