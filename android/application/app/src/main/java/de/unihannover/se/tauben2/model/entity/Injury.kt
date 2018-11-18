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