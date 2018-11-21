package de.unihannover.se.tauben2.model.dao

import androidx.room.Dao
import de.unihannover.se.tauben2.model.entity.User

/**
 * This class provides methods for accessing User objects in the Local Database
 */
@Dao
interface UserDao : BaseDao<User> {

}