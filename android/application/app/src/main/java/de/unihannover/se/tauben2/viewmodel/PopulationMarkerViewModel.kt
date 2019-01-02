package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker

class PopulationMarkerViewModel(context: Context) : BaseViewModel(context) {
    val populationMarkers: LiveDataRes<List<PopulationMarker>> = repository.getPigeonCounters()
    fun postCounterValue(value: CounterValue) = repository.postCounterValue(value)
}