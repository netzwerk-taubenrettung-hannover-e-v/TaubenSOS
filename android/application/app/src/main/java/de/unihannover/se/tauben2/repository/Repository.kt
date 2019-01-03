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
import de.unihannover.se.tauben2.model.database.entity.DatabaseEntity
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
        const val LOGIN_TOKEN_KEY = "authToken"
        const val LOGIN_USERNAME_KEY = "username"
    }

    private fun <T: DatabaseEntity> setItemUpdateTimestamps(vararg items: T) {
        items.forEach { it.lastUpdated = System.currentTimeMillis() }
    }

    private inline fun <reified T> getItemsToDelete(newItems: Collection<T>, oldItems: Collection<T>) = oldItems.minus(newItems).toTypedArray()

    // TODO Maybe insert and delete in one query
    fun getCases() = object : NetworkBoundResource<List<Case>, List<Case>>(appExecutors) {
        override fun saveCallResult(item: List<Case>) {
            database.caseDao().delete(*getItemsToDelete(item, loadFromDb().value ?: listOf()))
            Case.setLastAllUpdatedToNow()
            setItemUpdateTimestamps(*item.toTypedArray())
            database.caseDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<Case>?)= Case.shouldFetch()

        override fun loadFromDb()= database.caseDao().getCases()

        override fun createCall()= service.getCases(getToken())

    }.getAsLiveData()

    fun getCase(id: Int) = object : NetworkBoundResource<Case, Case>(appExecutors) {
        override fun saveCallResult(item: Case) {
            setItemUpdateTimestamps(item)
            database.caseDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: Case?) = data?.shouldFetch() ?: true

        override fun loadFromDb() = database.caseDao().getCase(id)

        override fun createCall() = service.getCase(getToken(), id)

    }.getAsLiveData()

    fun getUsers() = object : NetworkBoundResource<List<User>, List<User>>(appExecutors) {
        override fun saveCallResult(item: List<User>) {
            database.userDao().delete(*getItemsToDelete(item, loadFromDb().value ?: listOf()))
            setItemUpdateTimestamps(*item.toTypedArray())
            database.userDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<User>?) = User.shouldFetch()

        override fun loadFromDb() = database.userDao().getUsers()

        override fun createCall() = service.getUsers(getToken())

    }.getAsLiveData()

    fun getUser(username: String) = object : NetworkBoundResource<User, User>(appExecutors) {

        override fun saveCallResult(item: User) {
            setItemUpdateTimestamps(item)
            database.userDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: User?) = data?.shouldFetch() ?: true

        override fun loadFromDb() = database.userDao().getUser(username)

        override fun createCall() = service.getUser(getToken(), username)

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
            val res = service.getNews(getToken())
            return res
        }

    }.getAsLiveData()*/

    fun getPigeonCounters() = object : NetworkBoundResource<List<PopulationMarker>, List<PopulationMarker>>(appExecutors) {
        override fun saveCallResult(item: List<PopulationMarker>) {
            database.populationMarkerDao().delete(*getItemsToDelete(item, loadFromDb().value ?: listOf()))
            setItemUpdateTimestamps(*item.toTypedArray())
            database.populationMarkerDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<PopulationMarker>?) = PopulationMarker.shouldFetch()

        override fun loadFromDb() = database.populationMarkerDao().getAllPigeonCounters()

        override fun createCall() = service.getPigeonCounters(getToken())

    }.getAsLiveData()


    fun postNewMarker(marker: PopulationMarker) = object : AsyncDataRequest<PopulationMarker, PopulationMarker>(appExecutors) {
        override fun fetchUpdatedData(resultData: PopulationMarker): LiveDataRes<PopulationMarker> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: PopulationMarker) {
            setItemUpdateTimestamps(updatedData)
            database.populationMarkerDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: PopulationMarker): LiveDataRes<PopulationMarker> {
            return service.postNewMarker(requestData)
        }

    }.send(marker, enableRefetching = false)

    fun postCounterValue(value: CounterValue) = object : AsyncDataRequest<CounterValue, CounterValue>(appExecutors) {
        override fun fetchUpdatedData(resultData: CounterValue): LiveDataRes<CounterValue> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: CounterValue) {
            val marker = database.populationMarkerDao().getPopulationMarker(updatedData.populationMarkerID)
            marker.values += updatedData
            setItemUpdateTimestamps(updatedData)
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
            setItemUpdateTimestamps(updatedData)
            database.caseDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: Case): LiveDataRes<Case> {
            return service.sendCase(getToken(), requestData)
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
            setItemUpdateTimestamps(updatedData)
            database.caseDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: Case): LiveDataRes<Case> {
            requestData.caseID?.let {
                return service.updateCase(getToken(), it, requestData)
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
            requestData.caseID?.let { return service.deleteCase(getToken(), it) }
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
            setItemUpdateTimestamps(updatedData)
            database.userDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: User): LiveDataRes<User> {
            return service.register(getToken(), requestData)
        }

    }.send(user, enableRefetching = false)

    /**
     * Makes a login request, waits for its result and saves the authorization getToken.
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
                    sp.edit()
                            .putString(LOGIN_TOKEN_KEY, response.body()?.token)
                            .putString(LOGIN_USERNAME_KEY, user.username)
                            .apply()
                    Log.d(LOG_TAG, "Token saved")
                }
                response.code() == 401 -> throw Exception("Wrong username or password")
                else -> throw Exception(response.errorBody().toString())
            }
        })
        threadPool.shutdown()

        future.get()
    }

    fun getOwnerUsername() = sp.getString(LOGIN_USERNAME_KEY, null)

    fun logout() = object : AsyncDeleteRequest<String>(appExecutors) {

        override fun deleteFromDB(requestData: String) {
            sp.edit().remove(LOGIN_TOKEN_KEY).remove(LOGIN_USERNAME_KEY).apply()
        }

        override fun createCall(requestData: String): Call<Void> {
            return service.logout(requestData)
        }

    }.send(getToken())

    fun updatePermissions(auth: Auth) = object : AsyncDataRequest<User, Auth>(appExecutors) {
        override fun fetchUpdatedData(resultData: User): LiveDataRes<User> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: User) {
            setItemUpdateTimestamps(updatedData)
            database.userDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: Auth): LiveDataRes<User> {
            return service.updatePermissions(getToken(), requestData, requestData.username)
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

    private fun getToken() = sp.getString(LOGIN_TOKEN_KEY, "") ?: throw Exception("Auth getToken is null!")
}