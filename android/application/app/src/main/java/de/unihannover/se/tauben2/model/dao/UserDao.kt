package de.unihannover.se.tauben2.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.entity.User

/**
 * This class provides methods for accessing User objects in the Local Database
 */
@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM `user`")
    fun getUsers(): LiveData<List<User>>
}