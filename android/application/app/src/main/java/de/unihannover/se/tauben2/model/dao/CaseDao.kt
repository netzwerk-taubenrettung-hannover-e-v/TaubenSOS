package de.unihannover.se.tauben2.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.unihannover.se.tauben2.model.entity.Case

/**
 * This class provides methods for accessing Case objects in the Local Database
 */
@Dao
interface CaseDao : BaseDao<Case> {

    @Query("SELECT * FROM `case` WHERE caseID = :caseId")
    fun getCase(caseId: Int): LiveData<Case>

    @Query("SELECT * FROM `case`")
    fun getCases(): LiveData<List<Case>>
}