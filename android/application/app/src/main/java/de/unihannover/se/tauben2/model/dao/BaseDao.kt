package de.unihannover.se.tauben2.model.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

/**
 * This interface holds a collection of common methods that can be inherited by Dao objects
 */
interface BaseDao<T> {
    /**
     * takes one or more objects and inserts them into the RoomDatabase
     * @param obj objects to be inserted
     */
    @Insert
    fun insert(vararg obj: T)


    /**
     * takes one or more objects and updates the corresponding RoomDatabase entries based on their
     * keys
     * @param obj objects to be updated
     */
    @Update
    fun update(vararg obj: T)


    /**
     * takes one or more objects and deletes the corresponding RoomDatabase entries based on their
     * keys
     * @param obj objects to be deleted
     */
    @Delete
    fun delete(vararg obj: T)
}