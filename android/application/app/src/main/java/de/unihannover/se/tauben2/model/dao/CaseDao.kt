package de.unihannover.se.tauben2.model.dao

import androidx.room.Dao
import de.unihannover.se.tauben2.model.entity.Case

/**
 * This class provides methods for accessing Case objects in the Local Database
 */
@Dao
abstract class CaseDao : BaseDao<Case> {

}