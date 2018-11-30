package de.unihannover.se.tauben2.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Entity for storing a checklist of injuries that a pigeon of a certain case has
 */
@Entity(tableName = "injury")
data class Injury(@PrimaryKey(autoGenerate = true) val id: Int,
                  var footOrLeg: Boolean,
                  var wing: Boolean,
                  var head: Boolean,
                  var openWound: Boolean,
                  var paralyzedOrFlightless: Boolean,
                  var chick: Boolean,
                  var other: Boolean
)

/*fun getInjuriesAsStringList() : List<String>{
    val injuryList = mutableListOf<String>()
    if(footOrLeg) injuryList.add("Verletzter Fuß")
    if(wing) injuryList.add("Verletzter Flügel")
    if(head) injuryList.add("Verletzter Kopf")
    if(openWound) injuryList.add("Offene Wunde")
    if(paralyzedOrFlightless) injuryList.add("Bewegungs- oder Flugunfähig")
    if(chick) injuryList.add("Küken")
    if(other) injuryList.add("Sonstige:")                                       //TODO: Add actual text of other field either here or in CasesRecyclerFragment.kt
    return injuryList
}*/