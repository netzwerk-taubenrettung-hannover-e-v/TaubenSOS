package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.stat.PopulationStat

class StatsViewModel(context: Context) : BaseViewModel(context) {
    fun getPopulationStats(fromTime: Long, untilTime: Long, latNE: Double, lonNE: Double,
                           latSW: Double, lonSW: Double): LiveDataRes<List<PopulationStat>> =
            repository.getPopulationStats(fromTime, untilTime, latNE, lonNE, latSW, lonSW)

    fun getReportStats(fromTime: Long, untilTime: Long, latNE: Double, lonNE: Double,
                       latSW: Double, lonSW: Double) =
            repository.getPigeonNumberStats(fromTime, untilTime, latNE, lonNE, latSW, lonSW)

    fun getInjuryStats(fromTime: Long, untilTime: Long, latNE: Double, lonNE: Double,
                       latSW: Double, lonSW: Double) =
            repository.getInjuryStat(fromTime, untilTime, latNE, lonNE, latSW, lonSW)
}