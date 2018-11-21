package de.unihannover.se.tauben2.repository

import androidx.lifecycle.LiveData
import de.unihannover.se.tauben2.AppExecutors
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.LocalDatabase
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.network.NetworkService
import de.unihannover.se.tauben2.model.network.Resource

/**
 * Interface between data and view model. Should only be accessed from any view model class.
 *
 * @param database Instance of local SQLite database
 * @param service Instance of service interface with REST API routes
 * @param appExecutors Instance of app executors class for different threads
 *
 */
class Repository(private val database: LocalDatabase, private val service: NetworkService, private val appExecutors: AppExecutors = AppExecutors.INSTANCE){

    fun getCases() = object : NetworkBoundResource<List<Case>, List<Case>>(appExecutors) {
        override fun saveCallResult(item: List<Case>) {
            database.caseDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<Case>?): Boolean {
            return true
        }

        override fun loadFromDb() = database.caseDao().getCases()

        override fun createCall()= service.getCases()

    }.getAsLiveData()

    fun getCase(id: Int) = object : NetworkBoundResource<Case, Case>(appExecutors) {
        override fun saveCallResult(item: Case) {
            database.caseDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: Case?): Boolean {
            return true
        }

        override fun loadFromDb() = database.caseDao().getCase(id)

        override fun createCall() = service.getCase(id)

    }.getAsLiveData()
}