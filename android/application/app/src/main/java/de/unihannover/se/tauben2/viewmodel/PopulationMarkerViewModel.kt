package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.model.network.Resource

class PopulationMarkerViewModel(context: Context) : BaseViewModel(context) {
    val populationMarkers: LiveDataRes<List<PopulationMarker>> = repository.getPigeonCounters()
    fun postCounterValue(value: CounterValue) = repository.postCounterValue(value)
    fun postNewMarker(marker: PopulationMarker) = repository.postNewMarker(marker)
    fun deleteMarker(marker: PopulationMarker) = repository.deleteMarker(marker)

    fun reloadMarkerFromServer(successFunction : () -> Any) {
        val result = repository.getPigeonCounters()
        result.observeForever(object : Observer<Resource<List<PopulationMarker>>> {
            override fun onChanged(t: Resource<List<PopulationMarker>>?) {
                if(t?.status?.isSuccessful() == true) {
                    successFunction()
                    result.removeObserver(this)
                }
                if(t?.hasError() == true)
                    result.removeObserver(this)

            }

        })
    }

}