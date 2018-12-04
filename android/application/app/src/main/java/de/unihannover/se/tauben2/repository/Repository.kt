package de.unihannover.se.tauben2.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.AppExecutors
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.LocalDatabase
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.Media
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
class Repository(private val database: LocalDatabase, private val service: NetworkService, private val appExecutors: AppExecutors = AppExecutors.INSTANCE) {

    private val LOG_TAG = this::class.java.simpleName

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

    /**
     * Sends case to server and inserts the answer from the server into the local database
     * @param case Case which is sent to the server for creating it. Make sure that all attributes
     * the api doesn't accept are set to null
     */
    fun sendCase(case: Case) {
        /* NOTE: NetworkBoundResource doesn't really fit as abstraction for sending cases
        (e.g. no loadFromDb needed or LiveData for the ViewModel returned)
        So I pretty much copied this part from NetworkBoundResource. Maybe replace this function by another
        abstract class for sending api POST requests? */
        val apiResponseCase = service.sendCase(case)
        // add observer to LiveData resource
        apiResponseCase.observeForever(object : Observer<Resource<Case>> {
            override fun onChanged(response: Resource<Case>?) {
                if (response?.status?.isSuccessful() == true) {
                    appExecutors.diskIO().execute {
                        val responseCase = response.data
                        responseCase?.let {
                            database.caseDao().insertOrUpdate(it)
                            Log.d(LOG_TAG, "inserted received case")

                            // case successfully added to database, can remove observer
                            appExecutors.mainThread().execute {
                                apiResponseCase.removeObserver(this)
                            }
                        }
                    }
                }
            }
        })

    }
}