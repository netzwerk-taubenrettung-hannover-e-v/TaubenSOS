package de.unihannover.se.tauben2.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.database.entity.stat.PigeonNumberStat

@Dao
interface PigeonNumberStatDao : BaseDao<PigeonNumberStat> {
    @Query("""
        SELECT * FROM pigeon_stats
        WHERE day >= :fromTime and day <= :untilTime
        and latNE <= :latNE and lonNE <= :lonNE and latSW >= :latSW and lonSW >= :lonSW
        """)
    fun getPigeonNumberStats(fromTime: Long, untilTime: Long, latNE: Double, lonNE: Double, latSW: Double,
                             lonSW: Double): LiveData<List<PigeonNumberStat>>
}