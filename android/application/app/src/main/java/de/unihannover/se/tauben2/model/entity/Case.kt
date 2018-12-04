package de.unihannover.se.tauben2.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.view.recycler.RecyclerItem
import kotlinx.android.parcel.Parcelize

/**
 * represents the case of an injured pigeon
 */
@Parcelize
@Entity(tableName = "case"/*,
        foreignKeys = [
            ForeignKey(entity = Injury::class, parentColumns = ["id"], childColumns = ["injury_id"])
        ]*/)
data class Case(@PrimaryKey var caseID: Int?,
                var additionalInfo: String? = null,

                var isClosed: Boolean = false,
                var isWeddingPigeon: Boolean = false,
                var isCarrierPigeon: Boolean = false,

                var latitude: Double = -1.0,
                var longitude: Double = -1.0,

                var rescuer: String? = null,
                var priority: Int = -1,
                var timestamp: Long = -1,
                var phone: String = "",
                var wasFoundDead: Boolean = false
//                var media: List<String>,

//                @ColumnInfo(name = "injury_id") var injury: Int
) : RecyclerItem, MapMarkable, Parcelable {

    override fun getMarker(): MarkerOptions = MarkerOptions().position(LatLng(latitude, longitude)).title("Priorität: $priority").snippet(additionalInfo)

    override fun getType() = RecyclerItem.Type.ITEM

    fun getSinceString(): String {
        val diff = (System.currentTimeMillis()/1000 - timestamp).toDouble() / 60 //in minutes
        var res = ""
        when {
            diff > 1440 -> return "${(diff/1440).toInt()}  " + if(diff < 2880) "Tag" else "Tagen"
            diff >= 60 -> res = "${(diff/60).toInt()} h"
        }
        return res + " ${ Math.round(diff % 60) } min"
    }
}