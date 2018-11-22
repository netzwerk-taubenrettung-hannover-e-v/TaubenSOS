package de.unihannover.se.tauben2.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.entity.PigeonCounter

/**
 * This class provides methods for accessing Pigeon Counter objects in the Local Database
 */
@Dao
interface PigeonCounterDao : BaseDao<PigeonCounter> {

    /**
     * @param latitude latitude coordinate where pigeons were counted
     * @param longitude longitude coordinate where pigeons were counted
     * @param from timestamp for earliest pigeon counter object to be retrieved
     * @param to timestamp for latest pigeon counter object to be retrieved
     */
    @Query("""
        SELECT *
        FROM population
        WHERE latitude = :latitude AND longitude = :longitude
        AND timestamp BETWEEN :from AND :to
        """)
    fun getPigeonCounters(latitude: Double, longitude: Double, from: Long, to: Long): LiveData<List<PigeonCounter>>
}