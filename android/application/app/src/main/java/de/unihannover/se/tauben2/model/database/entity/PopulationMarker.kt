package de.unihannover.se.tauben2.model.database.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.App.Companion.context
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.database.converter.CounterValueConverter
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlin.math.roundToInt


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

                            var values: MutableList<CounterValue>
) : MapMarkable, DatabaseEntity(), Parcelable {

    override val refreshCooldown: Long
        get() = 1000 * 60 * 60 * 24 // 24 hours

    override fun getMarker(): MarkerOptions {
        val totalPigeonCount = if(values.isNotEmpty())
            (values.fold(0) { sum, element -> sum + element.pigeonCount }.toDouble()/values.size*100).roundToInt()/100.0
        else -1.0
        val mo = MarkerOptions().position(LatLng(latitude, longitude))
        return if(totalPigeonCount == -1.0) mo.title(context.getString(R.string.click_to_add_values)) else mo.title("${context.getString(R.string.average)} $totalPigeonCount")
    }

    companion object: AllUpdatable {
        override val refreshAllCooldown: Long
            get() = 0 //1000 * 60 * 15 // 30 min

    }
}
