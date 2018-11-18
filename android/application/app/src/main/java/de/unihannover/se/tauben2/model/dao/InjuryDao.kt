package de.unihannover.se.tauben2.model.dao

import androidx.room.Dao
import de.unihannover.se.tauben2.model.entity.Injury

/**
 * This class provides methods for accessing Injury objects in the Local Database
 */
@Dao
abstract class InjuryDao : BaseDao<Injury> {

}