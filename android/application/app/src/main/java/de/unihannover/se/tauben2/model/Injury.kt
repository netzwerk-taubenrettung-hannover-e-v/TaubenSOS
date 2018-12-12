package de.unihannover.se.tauben2.model

import android.os.Parcelable
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.recycler.RecyclerItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Injury (var fledgling: Boolean,
                   var footOrLeg: Boolean,
                   var headOrEye: Boolean,
                   var openWound: Boolean,
                   var other: Boolean,
                   var paralyzedOrFlightless: Boolean,
                   var wing: Boolean) : Parcelable, RecyclerItem {

    override fun getType(): RecyclerItem.Type = RecyclerItem.Type.ITEM

    fun toStringList(): List<String> {
        val injuryList = mutableListOf<String>()
        if(footOrLeg) injuryList.add(App.context.getString(R.string.injury_foot_leg))
        if(wing) injuryList.add(App.context.getString(R.string.injury_wings))
        if(headOrEye) injuryList.add(App.context.getString(R.string.injury_head_eye))
        if(openWound) injuryList.add(App.context.getString(R.string.injury_open_wound))
        if(paralyzedOrFlightless) injuryList.add(App.context.getString(R.string.injury_paralyzed_flightless))
        if(fledgling) injuryList.add(App.context.getString(R.string.injury_fledgling))
        return injuryList
    }

}