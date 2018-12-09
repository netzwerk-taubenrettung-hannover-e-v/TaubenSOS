package de.unihannover.se.tauben2.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.unihannover.se.tauben2.model.dao.CaseDao
import de.unihannover.se.tauben2.model.dao.InjuryDao
import de.unihannover.se.tauben2.model.dao.PigeonCounterDao
import de.unihannover.se.tauben2.model.dao.UserDao
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.InjuryEntity
import de.unihannover.se.tauben2.model.entity.PigeonCounter
import de.unihannover.se.tauben2.model.entity.User

/**
 * class with capability to create and retrieve a RoomDatabase singleton object which represents
 * the local SQLite Database
 */
@Database(entities = [Case::class, InjuryEntity::class, User::class, PigeonCounter::class], exportSchema = false, version = 1)
@TypeConverters(StringListConverter::class, PermissionConverter::class)
abstract class LocalDatabase : RoomDatabase() {

    companion object {
        @Volatile
        var DATABASE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            if (DATABASE == null) {
                synchronized(LocalDatabase::class) {
                    DATABASE = buildDatabase(context)
                }
            }
            return DATABASE
                    ?: throw IllegalAccessError("Can't access the local database. Some unknown error occurred.")
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                "local-database"
        ).build()
    }

    abstract fun caseDao(): CaseDao

    abstract fun injuryDao(): InjuryDao

    abstract fun userDao(): UserDao

    abstract fun pigeonCounterDao(): PigeonCounterDao

}
