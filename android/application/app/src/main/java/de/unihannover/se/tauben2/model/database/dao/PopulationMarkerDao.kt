package de.unihannover.se.tauben2.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker

/**
 * This class provides methods for accessing PopulationMarker objects in the Local Database
 */
@Dao
interface PopulationMarkerDao : BaseDao<PopulationMarker> {

    /**
     * @return LiveData of List of all PopulationMarkers
     */
    @Query("SELECT * FROM population")
    fun getAllPigeonCounters(): LiveData<List<PopulationMarker>>

    @Query("SELECT * FROM population WHERE populationMarkerID= :id")
    fun getPopulationMarker(id: Int): PopulationMarker
}