package de.unihannover.se.tauben2.repository

import android.util.Log
import androidx.lifecycle.LiveData
import de.unihannover.se.tauben2.AppExecutors
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.LocalDatabase
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.PigeonCounter
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.model.network.NetworkService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Interface between data and view model. Should only be accessed from any view model class.
 *
 * @param database Instance of local SQLite database
 * @param service Instance of service interface with REST API routes
 * @param appExecutors Instance of app executors class for different threads
 *
 */
class Repository(private val database: LocalDatabase, private val service: NetworkService, private val appExecutors: AppExecutors = AppExecutors.INSTANCE) {

    companion object {
        private val LOG_TAG = Repository::class.java.simpleName
    }

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
    fun sendCase(case: Case, mediaItems: List<ByteArray>) = object : AsyncDataRequest<Case, Case>(appExecutors) {
        override fun saveCallResult(resultData: Case) {
            // update db
            database.caseDao().insertOrUpdate(resultData)

            // amazon urls for upload
            val urls = resultData.media
            uploadPictures(mediaItems, urls)
        }

        override fun createCall(requestData: Case): LiveDataRes<Case> {
            return service.sendCase(requestData)
        }

    }.send(case)

    /**
     * Updates case on server via PUT request
     * @param case the case with updated values
     */
    fun updateCase(case: Case, mediaItems: List<ByteArray>) = object : AsyncDataRequest<Case, Case>(appExecutors) {
        override fun saveCallResult(resultData: Case) {
            // TODO somehow figure out a good way to update pictures
            //uploadPictures(mediaItems, uploadUrls)

            database.caseDao().insertOrUpdate(resultData)
        }

        override fun createCall(requestData: Case): LiveDataRes<Case> {
            requestData.caseID?.let {
                return service.updateCase(it, requestData)
            }
            throw Exception("Case id must not be null!")
        }

    }.send(case)

    /**
     * Deletes case from server via DELETE request and from local database
     * @param case The case to be deleted
     */
    fun deleteCase(case: Case) = object : AsyncDeleteRequest<Case>(appExecutors) {
        override fun deleteFromDB(requestData: Case) {
            database.caseDao().delete(requestData)
        }

        override fun createCall(requestData: Case): Call<Void> {
            requestData.caseID?.let { return service.deleteCase(it) }
            throw Exception("Case id must not be null!")
        }
    }.send(case)

    /**
     * Sends a PigeonCounter object to the server
     * @param pigeonCounter PigeonCounter object to be sent
     */
    fun sendPigeonCounter(pigeonCounter: PigeonCounter) =
    // TODO update Unit with actual return type when known (this will most likely be Void)
            object : AsyncDataRequest<Unit, PigeonCounter>(appExecutors) {
                override fun saveCallResult(resultData: Unit) {
                    // nothing to save yet
                }

                override fun createCall(requestData: PigeonCounter): LiveDataRes<Unit> {
                    return service.sendPigeonCounter(requestData)
                }

            }.send(pigeonCounter)


    /**
     * Helper function for uploading media files to their corresponding upload urls
     * The urls and files need to have the same length and order
     * @param mediaItems List of mediaItems items
     * @param urls  List of urls for upload request
     */
    fun uploadPictures(mediaItems: List<ByteArray>, urls: List<String>) {
        if (mediaItems.size != urls.size)
            throw Exception("The number of upload urls and media items is different!")

        // upload mediaItems to amazon
        for ((url, mediaItem) in urls.zip(mediaItems)) {

            val parsedPicture = RequestBody.create(
                    MediaType.parse("application/octet"), mediaItem)

            // enqueue a new call for each mediaItem
            val call = service.uploadCasePicture(url, parsedPicture)
            call.enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(LOG_TAG, "File upload failed!")
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d(LOG_TAG, "File upload request successful!")
                }

            })
        }
    }
}