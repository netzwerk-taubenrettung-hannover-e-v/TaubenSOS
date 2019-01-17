package de.unihannover.se.tauben2.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.database.entity.stat.InjuryStat

/**
 * This class provides methods for accessing InjuryStat objects in the Local Database
 */
@Dao
interface InjuryStatDao : BaseDao<InjuryStat> {

    @Query("""
        SELECT * FROM injury_stats
        WHERE fromTime = :fromTime and untilTime = :untilTime
    """)
    fun getInjuryStat(fromTime: Long, untilTime: Long): LiveData<InjuryStat>
}