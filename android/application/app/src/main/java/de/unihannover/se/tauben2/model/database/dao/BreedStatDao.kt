package de.unihannover.se.tauben2.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.database.entity.stat.BreedStat

/**
 * This class provides methods for accessing BreedStat objects in the Local Database
 */
@Dao
interface BreedStatDao : BaseDao<BreedStat> {

    @Query("""
        SELECT * FROM breed_stats
    """)
    fun getBreedStat(): LiveData<BreedStat>

    @Query("""
        DELETE FROM breed_stats
    """)
    fun deleteOldStats()
}