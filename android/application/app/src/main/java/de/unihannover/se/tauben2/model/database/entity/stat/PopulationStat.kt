package de.unihannover.se.tauben2.model.database.entity.stat

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "population_stats")
class PopulationStat(@PrimaryKey
                     val day: Long,
                     val count: Int,
                     latNE: Double?,
                     lonNE: Double?,
                     latSW: Double?,
                     lonSW: Double?)
    : PlaceStat(latNE, lonNE, latSW, lonSW)
