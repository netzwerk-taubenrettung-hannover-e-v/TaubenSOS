package de.unihannover.se.tauben2.model

import android.os.Parcelable
import de.unihannover.se.tauben2.model.database.entity.DatabaseEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CounterValue(var pigeonCount: Int,
                        var populationMarkerID: Int,
                        var timestamp: Long) : DatabaseEntity(), Parcelable {
    override val refreshCooldown: Long
        get() = 1000 * 60 * 60 * 3 // 3 h

}