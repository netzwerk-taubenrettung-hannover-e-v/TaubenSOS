package de.unihannover.se.tauben2.model.dao

import androidx.room.Dao
import de.unihannover.se.tauben2.model.entity.PigeonCounter

/**
 * This class provides methods for accessing Pigeon Counter objects in the Local Database
 */
@Dao
interface PigeonCounterDao : BaseDao<PigeonCounter> {

}