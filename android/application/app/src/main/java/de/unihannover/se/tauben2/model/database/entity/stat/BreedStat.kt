package de.unihannover.se.tauben2.model.database.entity.stat

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "breed_stats")
data class BreedStat(@PrimaryKey(autoGenerate = true) val id: Int,
                     val carrierPigeon: Int,
                     val commonWoodPigeon: Int,
                     val fancyPigeon: Int,
                     val feralPigeon: Int,
                     val undefined: Int,
                     var fromTime: Long?,
                     var untilTime: Long?)