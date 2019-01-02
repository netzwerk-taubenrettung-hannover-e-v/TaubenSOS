package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker

class PigeonCounterViewModel(context: Context) : BaseViewModel(context) {
    val pigeonCounters: LiveDataRes<List<PopulationMarker>> = repository.getPigeonCounters()
}