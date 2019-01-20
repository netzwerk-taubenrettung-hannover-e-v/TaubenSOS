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
        WHERE fromTime = :fromTime and untilTime = :untilTime
    """)
    fun getBreedStat(fromTime: Long, untilTime: Long): LiveData<BreedStat>
}