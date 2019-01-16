package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.PopulationStat

class StatsViewModel(context: Context) : BaseViewModel(context) {
    fun getPopulationStats(fromTime: Long, untilTime: Long, latNE: Double, lonNE: Double,
                           latSW: Double, lonSW: Double): LiveDataRes<List<PopulationStat>> =
            repository.getPopulationStats(fromTime, untilTime, latNE, lonNE, latSW, lonSW)

    fun getPigeonNumberStats(fromTime: Long, untilTime: Long, latNE: Double, lonNE: Double,
                             latSW: Double, lonSW: Double) =
            repository.getPigeonNumberStats(fromTime, untilTime, latNE, lonNE, latSW, lonSW)
}