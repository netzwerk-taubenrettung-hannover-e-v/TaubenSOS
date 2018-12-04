package de.unihannover.se.tauben2.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Injury (var fledgling: Boolean,
                   var footOrLeg: Boolean,
                   var headOrEye: Boolean,
                   var openWound: Boolean,
                   var other: Boolean,
                   var paralyzedOrFlightless: Boolean,
                   var wing: Boolean) : Parcelable {

    fun toStringList(): List<String> {
        val injuryList = mutableListOf<String>()
        if(footOrLeg) injuryList.add("Verletzter Fuß")
        if(wing) injuryList.add("Verletzter Flügel")
        if(headOrEye) injuryList.add("Verletzter Kopf")
        if(openWound) injuryList.add("Offene Wunde")
        if(paralyzedOrFlightless) injuryList.add("Bewegungs- oder Flugunfähig")
        if(fledgling) injuryList.add("Küken")
        return injuryList
    }

}