package de.unihannover.se.tauben2.repository

import androidx.lifecycle.LiveData
import de.unihannover.se.tauben2.AppExecutors
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.LocalDatabase
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.PigeonCounter
import de.unihannover.se.tauben2.model.entity.User
import de.unihannover.se.tauben2.model.network.NetworkService

/**
 * Interface between data and view model. Should only be accessed from any view model class.
 *
 * @param database Instance of local SQLite database
 * @param service Instance of service interface with REST API routes
 * @param appExecutors Instance of app executors class for different threads
 *
 */
class Repository(private val database: LocalDatabase, private val service: NetworkService, private val appExecutors: AppExecutors = AppExecutors.INSTANCE) {


    fun getCases() = object : NetworkBoundResource<List<Case>, List<Case>>(appExecutors) {
        override fun saveCallResult(item: List<Case>) {
            database.caseDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<Case>?): Boolean {
            return true
        }

        override fun loadFromDb(): LiveData<List<Case>> {
            val res = database.caseDao().getCases()
            return res
        }

        override fun createCall(): LiveDataRes<List<Case>> {
            val res = service.getCases()
            return res
        }

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

    fun getUsers() = object : NetworkBoundResource<List<User>, List<User>>(appExecutors) {
        override fun saveCallResult(item: List<User>) {
            database.userDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<User>?): Boolean {
            return true
        }

        override fun loadFromDb(): LiveData<List<User>> {
            val res = database.userDao().getUsers()
            return res
        }

        override fun createCall(): LiveDataRes<List<User>> {
            val res = service.getUsers()
            return res
        }

    }.getAsLiveData()

    fun getPigeonCounters() = object : NetworkBoundResource<List<PigeonCounter>, List<PigeonCounter>>(appExecutors) {
        override fun saveCallResult(item: List<PigeonCounter>) {
            database.pigeonCounterDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<PigeonCounter>?): Boolean {
            return true
        }

        override fun loadFromDb(): LiveData<List<PigeonCounter>> {
            return database.pigeonCounterDao().getAllPigeonCounters()
        }

        override fun createCall(): LiveDataRes<List<PigeonCounter>> {
            return service.getPigeonCounters()
        }

    }.getAsLiveData()

    /**
     * Sends case to server and inserts the answer from the server into the local database
     * @param case Case which is sent to the server for creating it. Make sure that all attributes
     * the api doesn't accept are set to null
     */
    fun sendCase(case: Case) = object : AsyncDataRequest<Case, Case>(appExecutors) {
        override fun saveCallResult(resultData: Case) {
            database.caseDao().insertOrUpdate(resultData)
        }

        override fun createCall(requestData: Case): LiveDataRes<Case> {
            return service.sendCase(requestData)
        }

    }.send(case)

    /**
     * Sends a PigeonCounter object to the server
     * @param pigeonCounter PigeonCounter object to be sent
     */
    fun sendPigeonCounter(pigeonCounter: PigeonCounter) =
    // TODO update Unit with actual return type when known
            object : AsyncDataRequest<Unit, PigeonCounter>(appExecutors) {
                override fun saveCallResult(resultData: Unit) {
                    // nothing to save yet
                }

                override fun createCall(requestData: PigeonCounter): LiveDataRes<Unit> {
                    return service.sendPigeonCounter(requestData)
                }

            }.send(pigeonCounter)
}