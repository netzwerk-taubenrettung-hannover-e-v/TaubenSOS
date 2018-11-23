package de.unihannover.se.tauben2.model.dao

import androidx.room.Dao
import de.unihannover.se.tauben2.model.entity.InjuryEntity

/**
 * This class provides methods for accessing InjuryEntity objects in the Local Database
 */
@Dao
interface InjuryDao : BaseDao<InjuryEntity> {

}