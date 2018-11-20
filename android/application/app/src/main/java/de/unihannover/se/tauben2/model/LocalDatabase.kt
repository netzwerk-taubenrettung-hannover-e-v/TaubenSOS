package de.unihannover.se.tauben2.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.Injury
import de.unihannover.se.tauben2.model.entity.User

/**
 * class with capability to create and retrieve a RoomDatabase singleton object which represents
 * the local SQLite Database
 */
@Database(entities = [Case::class, Injury::class, User::class, PigeonCounter::class], version = 1)
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
