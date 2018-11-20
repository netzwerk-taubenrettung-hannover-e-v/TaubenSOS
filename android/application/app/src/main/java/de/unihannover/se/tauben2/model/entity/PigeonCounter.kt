package de.unihannover.se.tauben2.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Entity for storing a checklist of injuries that a pigeon of a certain case has
 */
@Entity(tableName = "population")
data class PigeonCounter(@PrimaryKey(autoGenerate = true) val id: Int,
                  var coordinates: String, // TODO Coordinate class and converter?
                  var date: DateTime,
                  var numberOfPigeons: int,
)