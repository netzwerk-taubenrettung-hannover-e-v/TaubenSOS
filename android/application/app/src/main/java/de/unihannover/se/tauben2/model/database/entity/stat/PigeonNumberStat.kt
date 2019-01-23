package de.unihannover.se.tauben2.model.database.entity.stat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pigeon_stats")
class PigeonNumberStat(@PrimaryKey val day: Long,
                       val count: Int,
                       val sumFoundDead: Int,
                       val sumNotFound: Int,
                       latNE: Double?,
                       lonNE: Double?,
                       latSW: Double?,
                       lonSW: Double?)
    : PlaceStat(latNE, lonNE, latSW, lonSW)
