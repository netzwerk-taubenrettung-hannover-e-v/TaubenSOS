package de.unihannover.se.tauben2.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.entity.Media

@Dao
interface MediaDao: BaseDao<Media> {

    @Query("SELECT * FROM `media` WHERE caseID = :caseId")
    fun getMedia(caseId: Int): LiveData<List<Media>>

    @Query("SELECT * FROM `media`")
    fun getMedia(): LiveData<List<Media>>

}