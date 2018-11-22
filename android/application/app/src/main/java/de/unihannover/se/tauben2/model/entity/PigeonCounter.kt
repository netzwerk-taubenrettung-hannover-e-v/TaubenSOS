package de.unihannover.se.tauben2.model.entity

import androidx.room.Entity


/**
 * Entity for storing count data of pigeons
 * @param latitude latitude coordinate
 * @param longitude longitude coordinate
 * @param timestamp unix timestamp when the pigeons where counted
 */
@Entity(tableName = "population", primaryKeys = ["latitude", "longitude", "timestamp"])
data class PigeonCounter(var latitude: Double,
                         var longitude: Double,
                         var timestamp: Long,
                         var numberOfPigeons: Long
)