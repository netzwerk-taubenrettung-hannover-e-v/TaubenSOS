package de.unihannover.se.tauben2.model.database.entity.stat


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "injury_stats")
data class InjuryStat(@PrimaryKey(autoGenerate = true) val id: Int,
                      var sumStrappedFeet: Int,
                      var sumFledgling: Int,
                      var sumFootOrLeg: Int,
                      var sumHeadOrEye: Int,
                      var sumOpenWound: Int,
                      var sumOther: Int,
                      var sumParalyzedOrFlightless: Int,
                      var sumWing: Int,
                      var fromTime: Long?,
                      var untilTime: Long?)