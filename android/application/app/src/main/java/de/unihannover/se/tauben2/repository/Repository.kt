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
import de.unihannover.se.tauben2.model.database.entity.*
import de.unihannover.se.tauben2.model.network.NetworkService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
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
        const val GUEST_PHONE = "phone"
    }

    private fun <T : DatabaseEntity> setItemUpdateTimestamps(vararg items: T) {
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

        override fun shouldFetch(data: List<Case>?) = Case.shouldFetch()

        override fun loadFromDb() = database.caseDao().getCases()

        override fun createCall(): LiveDataRes<List<Case>> {
            return service.getCases(getToken())
        }

    }.getAsLiveData()

    fun getPopulationStats(fromTime: Long, untilTime: Long, latNE: Double, lonNE: Double,
                           latSW: Double, lonSW: Double) = object :
            NetworkBoundResource<List<PopulationStat>, List<PopulationStat>>(appExecutors) {
        override fun saveCallResult(item: List<PopulationStat>) {
            // set boundaries for database query
            item.forEach {
                it.latNE = latNE
                it.lonNE = lonNE
                it.latSW = latSW
                it.lonSW = lonSW
            }
            database.populationStatDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<PopulationStat>?) = true


        override fun loadFromDb(): LiveData<List<PopulationStat>> =
                database.populationStatDao().getPopulationStats(fromTime, untilTime, latNE, lonNE,
                        latSW, lonSW)


        override fun createCall(): LiveDataRes<List<PopulationStat>> {
            return service.getPopulationStats(getToken(), fromTime, untilTime, latNE, lonNE, latSW,
                    lonSW)
        }

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


    fun getNewsPost(feedID: Int) = object : NetworkBoundResource<News, News>(appExecutors) {
        override fun saveCallResult(item: News) {
            database.newsDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: News?): Boolean {
            return News.shouldFetch()
        }

        override fun loadFromDb(): LiveData<News> {
            return database.newsDao().getNewsPost(feedID)
        }

        override fun createCall(): LiveDataRes<News> {
            return service.getNewsPost(getToken(), feedID)
        }

    }.getAsLiveData()

    fun getNews() = object : NetworkBoundResource<List<News>, List<News>>(appExecutors) {
        override fun saveCallResult(item: List<News>) {
            database.newsDao().insertOrUpdate(item)
        }

        override fun shouldFetch(data: List<News>?): Boolean {
            return News.shouldFetch()
        }

        override fun loadFromDb(): LiveData<List<News>> {
            val res = database.newsDao().getNews()
            return res
        }

        override fun createCall(): LiveDataRes<List<News>> {
            val res = service.getNews(getToken())
            return res
        }

    }.getAsLiveData()

    fun sendNews(news: News) = object : AsyncDataRequest<News, News>(appExecutors) {
        override fun fetchUpdatedData(resultData: News): LiveDataRes<News> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: News) {
            database.newsDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: News): LiveDataRes<News> {
            return service.sendNews(getToken(), requestData)
        }

    }.send(news, enableRefetching = false)

    fun updateNews(news: News) = object : AsyncDataRequest<News, News>(appExecutors) {
        override fun fetchUpdatedData(resultData: News): LiveDataRes<News> {
            throw Exception("Re-fetching is disabled, don't try to force it!")
        }

        override fun saveUpdatedData(updatedData: News) {
            setItemUpdateTimestamps(updatedData)
            database.newsDao().insertOrUpdate(updatedData)
        }

        override fun createCall(requestData: News): LiveDataRes<News> {
            requestData.feedID?.let {
                return service.updateNews(getToken(), it, requestData)
            }
            throw Exception("Case id must not be null!")
        }
    }.send(news, false)

    fun getPigeonCounters() = object : NetworkBoundResource<List<PopulationMarker>, List<PopulationMarker>>(appExecutors) {
        override fun saveCallResult(item: List<PopulationMarker>) {
            database.populationMarkerDao().delete(*getItemsToDelete(item, loadFromDb().value
                    ?: listOf()))
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
            return service.postNewMarker(getToken(), requestData)
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
            return service.postCounterValue(getToken(), requestData, requestData.populationMarkerID)
        }

    }.send(value, enableRefetching = false)

    fun deleteMarker(marker: PopulationMarker) = object : AsyncDeleteRequest<PopulationMarker>(appExecutors) {
        override fun deleteFromDB(requestData: PopulationMarker) {
            database.populationMarkerDao().delete(requestData)
        }

        override fun createCall(requestData: PopulationMarker): Call<Void> {
            return service.deleteMarker(getToken(), requestData.populationMarkerID)
        }

    }.send(marker)

    /**
     * Sends case to server and inserts the answer from the server into the local database
     * @param case Case which is sent to the server for creating it. Make sure that all attributes
     * the api doesn't accept are set to null
     */
    fun sendCase(case: Case, mediaItems: List<ByteArray>) = object : AsyncDataRequest<Case, Case>(appExecutors) {

        override fun fetchUpdatedData(resultData: Case): LiveDataRes<Case> {
            // amazon upload urls
            val urls = mutableListOf<String>()

            while (urls.size != mediaItems.size) {
                urls.add(resultData.getMediaUploadURL())
            }

            appExecutors.networkIO().execute {
                uploadPictures(mediaItems, urls)
            }

            resultData.caseID?.let { return getCase(it) }
            throw Exception("Couldn't fetch updated case from server!")
        }

        override fun saveUpdatedData(updatedData: Case) {
            sp.edit().putString(GUEST_PHONE, updatedData.phone).apply()
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
            val urls = mutableListOf<String>()
            case.media.filter { it.toDelete }.forEachIndexed { index, m ->

                // if not enough new local media items exists then delete old server media items
                if (mediaItems.size <= index)

                    appExecutors.networkIO().execute {
                        service.deleteCaseMedia(getToken(), resultData.getMediaURL(m.mediaID))
                    }
                // else replace old server media items with new local media items
                else
                    urls.add(resultData.getMediaURL(m.mediaID))
            }

            while (urls.size != mediaItems.size) {
                urls.add(resultData.getMediaUploadURL())
//                urls = urls.takeLast(urls.size - 1).toMutableList()
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
                return service.updateCase(getToken(), it, requestData.copy(media = listOf()))
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

    fun getGuestPhone() = sp.getString(GUEST_PHONE, null)

    fun setGuestPhone(phone: String) {
        sp.edit().putString(GUEST_PHONE, phone).apply()
    }

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
            val call = if (url.endsWith("media"))
                service.uploadCaseMedia(getToken(), url, parsedPicture)
            else
                service.updateCaseMedia(getToken(), url, parsedPicture)

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

    fun deleteNews(news: News) = object : AsyncDeleteRequest<News>(appExecutors) {
        override fun deleteFromDB(requestData: News) {
            database.newsDao().delete(requestData)
        }

        override fun createCall(requestData: News): Call<Void> {
            requestData.feedID?.let { return service.deleteNews(getToken(), it) }
            throw Exception("Feed id must not be null!")
        }
    }.send(news)

    private fun getToken() = sp.getString(LOGIN_TOKEN_KEY, "")
            ?: throw Exception("Auth getToken is null!")
}