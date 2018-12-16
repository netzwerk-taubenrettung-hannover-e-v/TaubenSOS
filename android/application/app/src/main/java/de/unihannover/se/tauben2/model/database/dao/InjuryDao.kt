package de.unihannover.se.tauben2.model.database.dao

import androidx.room.Dao
import de.unihannover.se.tauben2.model.database.entity.InjuryEntity

/**
 * This class provides methods for accessing InjuryEntity objects in the Local Database
 */
@Dao
interface InjuryDao : BaseDao<InjuryEntity> {

}