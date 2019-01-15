package de.unihannover.se.tauben2.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "population_stats")
class PopulationStat(@PrimaryKey val date: Long,
                     val count: Int,
                     var latNE: Double?,
                     var lonNE: Double?,
                     var latSW: Double?,
                     var lonSW: Double?)
