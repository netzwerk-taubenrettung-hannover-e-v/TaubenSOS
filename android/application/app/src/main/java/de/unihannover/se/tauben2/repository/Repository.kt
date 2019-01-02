package de.unihannover.se.tauben2.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.AppExecutors
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.Auth
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.database.LocalDatabase
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.model.network.NetworkService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Interface between data and view model. Should only be accessed from any view model class.
 *
 * @param database Instance of local SQLite database
 * @param service Instance of service interface with REST API routes
 * @param appExecutors Instance of app executors class for different threads
 *
 */
class Repository(private val database: LocalDatabase, private val service: NetworkService, private val appExecutors: AppExecutors = AppExecutors.INSTANCE) {

    private val sp = App.context.getSharedPreferences("tauben2", Context.MODE_PRIVATE)

    companion object {
        private val LOG_TAG = Repository::class.java.simpleName
        private const val TOKEN_KEY = "authToken"
    }

    fun getCases() = object : NetworkBoundResource<List<Case>, List<Case>>(appExecutors) {
        override fun saveCallResult(item: List<Case>) {
            database.caseDao().insertOrUpdate(item)
            sp.edit().apply {
                putLong("caseLastLoaded", System.currentTimeMillis())
                apply()
            }
        }

        override fun shouldFetch(data: List<Case>?): Boolean {
            // 15 min
            return System.currentTimeMillis() - sp.getLong("caseLastLoaded", 0) > 900000
        }

        override fun loadFromDb(): LiveData<List<Case>> {
            val res = database.caseDao().getCases()
            return res
        }

        override fun createCall(): LiveDataRes<List<Case>> {
            val res = service.getCases(token())
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

        override fun createCall() = service.getCase(token(), id)

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
            val res = service.getUsers(token())
            return res
        }

    }.getAsLiveData()

    /*fun getNews() = object : NetworkBoundResource<List<News>, List<News>>(appExecutors) {
        override fun saveCallResult(item: List<News>) {
           database.newsDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<News>?): Boolean {
            return true
        }

        override fun loadFromDb(): LiveData<List<News>> {
            val res = database.newsDao().getNews()
            return res
        }

        override fun createCall(): LiveDataRes<List<News>> {
            val res = service.getNews(token())
            return res
        }

    }.getAsLiveData()*/

    fun getPigeonCounters() = object : NetworkBoundResource<List<PopulationMarker>, List<PopulationMarker>>(appExecutors) {
        override fun saveCallResult(item: List<PopulationMarker>) {
            database.populationMarkerDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<PopulationMarker>?): Boolean {
            return true
        }

        override fun loadFromDb(): LiveData<List<PopulationMarker>> {
            return database.populationMarkerDao().getAllPigeonCounters()
        }

        override fun createCall(): LiveDataRes<List<PopulationMarker>> {
            return service.getPigeonCounters(token())
        }

    }.getAsLiveData()

    fun postCounterValue(value: CounterValue) = object : AsyncDataRequest<CounterValue, CounterValue>(appExecutors) {
        override fun fetchUpdatedData(resultData: CounterValue): LiveDataRes<CounterValue> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: CounterValue) {
            val marker = database.populationMarkerDao().getPopulationMarker(updatedData.populationMarkerID)
            marker.values += updatedData
            database.populationMarkerDao().insertOrUpdate(marker)
        }

        override fun createCall(requestData: CounterValue): LiveDataRes<CounterValue> {
            return service.postCounterValue(requestData, requestData.populationMarkerID)
        }

    }.send(value, enableRefetching = false)

    /**
     * Sends case to server and inserts the answer from the server into the local database
     * @param case Case which is sent to the server for creating it. Make sure that all attributes
     * the api doesn't accept are set to null
     */
    fun sendCase(case: Case, mediaItems: List<ByteArray>) = object : AsyncDataRequest<Case, Case>(appExecutors) {

        override fun fetchUpdatedData(resultData: Case): LiveDataRes<Case> {
            // amazon upload urls
            val urls = resultData.media
            appExecutors.networkIO().execute {
                uploadPictures(mediaItems, urls)
            }

            resultData.caseID?.let { return getCase(it) }
            throw Exception("Couldn't fetch updated case from server!")
        }

        override fun saveUpdatedData(updatedData: Case) {
            database.caseDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: Case): LiveDataRes<Case> {
            return service.sendCase(token(), requestData)
        }

    }.send(case)

    /**
     * Updates case on server via PUT request
     * @param case the case with updated values
     */
    fun updateCase(case: Case, mediaItems: List<ByteArray>) = object : AsyncDataRequest<Case, Case>(appExecutors) {
        override fun fetchUpdatedData(resultData: Case): LiveDataRes<Case> {
            // amazon upload urls
            var urls = resultData.media
            while (urls.size != mediaItems.size) {
                urls = urls.takeLast(urls.size - 1)
            }
            if (urls.isNotEmpty()) {
                appExecutors.networkIO().execute {
                    uploadPictures(mediaItems, urls)
                }
            }
            resultData.caseID?.let { return getCase(it) }
            throw Exception("Couldn't fetch updated case from server!")
        }

        override fun saveUpdatedData(updatedData: Case) {
            database.caseDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: Case): LiveDataRes<Case> {
            requestData.caseID?.let {
                return service.updateCase(token(), it, requestData)
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
            requestData.caseID?.let { return service.deleteCase(token(), it) }
            throw Exception("Case id must not be null!")
        }
    }.send(case)

    /**
     * Creates a register request and saves the register data to the local database
     * @param user The user that should be created
     */
    fun register(user: User) = object : AsyncDataRequest<User, User>(appExecutors) {
        override fun fetchUpdatedData(resultData: User): LiveDataRes<User> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: User) {
            database.userDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: User): LiveDataRes<User> {
            return service.register(token(), requestData)
        }

    }.send(user, enableRefetching = false)

    /**
     * Makes a login request, waits for its result and saves the authorization token.
     * Throws Exception if login not successful
     * @param user The user who is trying to login
     */
    fun login(user: User) {
        val threadPool = Executors.newSingleThreadScheduledExecutor()
        val future = threadPool.submit(Callable {
            val call = service.login(user)
            val response = call.execute()
            when {
                response.isSuccessful -> {
                    sp.edit().putString(TOKEN_KEY, response.body()?.token).apply()
                    Log.d(LOG_TAG, "Token saved")
                }
                response.code() == 401 -> throw Exception("Wrong username or password")
                else -> throw Exception(response.errorBody().toString())
            }
        })
        threadPool.shutdown()

        future.get()
    }

    fun logout() = object : AsyncDeleteRequest<String>(appExecutors) {
        override fun deleteFromDB(requestData: String) {
            sp.edit().remove(TOKEN_KEY).apply()
        }

        override fun createCall(requestData: String): Call<Void> {
            return service.logout(requestData)
        }
    }.send(token())

    fun updatePermissions(auth: Auth) = object : AsyncDataRequest<User, Auth>(appExecutors) {
        override fun fetchUpdatedData(resultData: User): LiveDataRes<User> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: User) {
            database.userDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: Auth): LiveDataRes<User> {
            return service.updatePermissions(token(), requestData, requestData.username)
        }

    }.send(auth, enableRefetching = false)


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
                    Log.d(LOG_TAG, "Reason: ${t.message}")
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d(LOG_TAG, "File upload request successful!")
                }

            })
        }
    }

    private fun token(): String {
        val token = sp.getString(TOKEN_KEY, "")
        token?.let {
            return it
        }
        throw Exception("Auth token is null!")
    }
}