package de.unihannover.se.tauben2.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Entity for storing a checklist of injuries that a pigeon of a certain case has
 */
@Entity(tableName = "injury")
data class InjuryEntity(@PrimaryKey(autoGenerate = true) var id: Int,
                        var fledgling: Boolean,
                        var footOrLeg: Boolean,
                        var headOrEye: Boolean,
                        var openWound: Boolean,
                        var other: Boolean,
                        var paralyzedOrFlightless: Boolean,
                        var wing: Boolean
)