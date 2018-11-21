package de.unihannover.se.tauben2.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


/**
 * Entity for storing count data of pigeons
 */
@Entity(tableName = "population")
data class PigeonCounter(@PrimaryKey(autoGenerate = true) val id: Int,
                         var latitude: Double,
                         var longitude: Double,
//                         var date: Date,
                         var numberOfPigeons: Int
)