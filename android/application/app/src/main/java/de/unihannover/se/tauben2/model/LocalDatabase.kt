/*
package de.unihannover.se.tauben2.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    companion object {
        @Volatile
        var DATABASE: LocalDatabase? = null

        fun getDatabase(context: Context) = DATABASE ?: synchronized(this) {
            DATABASE ?: buildDatabase(context).also { DATABASE = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext,
                LocalDatabase::class.java,
                "local-database"
        ).build()
    }
}
*/