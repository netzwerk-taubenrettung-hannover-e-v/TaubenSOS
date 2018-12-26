package de.unihannover.se.tauben2.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.database.entity.News

/**
 * This class provides methods for accessing News objects in the Local Database
 */
@Dao
interface NewsDao : BaseDao<News> {

    @Query("SELECT * FROM `news`")
    fun getNews(): LiveData<List<News>>
}