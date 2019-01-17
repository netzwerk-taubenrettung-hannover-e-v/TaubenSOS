package de.unihannover.se.tauben2.model.database.entity.stat


import androidx.room.Entity


@Entity(tableName = "injury_stats")
class InjuryStat(var sumStrappedFeet: Int,
                 var sumFledgling: Int,
                 var sumFootOrLeg: Int,
                 var sumHeadOrEye: Int,
                 var sumOpenWound: Int,
                 var sumOther: Int,
                 var sumParalyzedOrFlightless: Int,
                 var sumWing: Int)