package de.unihannover.se.tauben2.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.unihannover.se.tauben2.model.database.converter.CounterValueConverter
import de.unihannover.se.tauben2.model.database.converter.ListConverter
import de.unihannover.se.tauben2.model.database.converter.PermissionConverter
import de.unihannover.se.tauben2.model.database.dao.*
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.model.database.entity.stat.BreedStat
import de.unihannover.se.tauben2.model.database.entity.stat.InjuryStat
import de.unihannover.se.tauben2.model.database.entity.stat.PigeonNumberStat
import de.unihannover.se.tauben2.model.database.entity.stat.PopulationStat

/**
 * class with capability to create and retrieve a RoomDatabase singleton object which represents
 * the local SQLite Database
 */
@Database(entities = [Case::class, User::class, PopulationMarker::class,
    News::class, PopulationStat::class, PigeonNumberStat::class, InjuryStat::class, BreedStat::class], exportSchema = false, version = 1)
@TypeConverters(ListConverter::class, PermissionConverter::class, CounterValueConverter::class)
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

    abstract fun userDao(): UserDao

    abstract fun newsDao(): NewsDao

    abstract fun populationMarkerDao(): PopulationMarkerDao

    abstract fun populationStatDao(): PopulationStatDao

    abstract fun pigeonNumberStatDao(): PigeonNumberStatDao

    abstract fun injuryStatDao(): InjuryStatDao

    abstract fun breedStatDao(): BreedStatDao

}
