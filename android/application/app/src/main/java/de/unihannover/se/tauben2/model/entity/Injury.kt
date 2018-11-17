package de.unihannover.se.tauben2.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/**
 * checklist for what injuries a pigeon has
 */
@Entity(tableName = "injury")
data class Injury(@PrimaryKey val id: Int,
                  var footOrLeg: Boolean,
                  var wing: Boolean,
                  var head: Boolean,
                  var openWound: Boolean,
                  var paralyzedOrFlightless: Boolean,
                  var chick: Boolean,
                  var other: Boolean
)