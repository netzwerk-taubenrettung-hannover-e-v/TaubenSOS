package de.unihannover.se.tauben2.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 * @param ResultType Type for the Resource data.
 * @param RequestType Type for the API response.
 */
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors){

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        // Send loading state to UI
        result.value = Resource.loading()
        val dbSource = this.loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            if(shouldFetch(it))
                fetchFromNetwork(dbSource)
            else
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            result.value = Resource.loading(newData)
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            response.apply {
                if(this?.status?.isSuccessful() == true) {
                    appExecutors.diskIO().execute {
                        processResponse(this)?.let {
                            saveCallResult(it)
                        }
                        appExecutors.mainThread().execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb()) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                } else {
                    onFetchFailed()
                    result.addSource(dbSource) { result.value = Resource.error (this?.message ?: "Unknown error occurred.")}
                }
            }
        }
    }

    @WorkerThread
    protected fun processResponse(response: Resource<RequestType>) = response.data

    @MainThread
    protected fun setValue(newValue: Resource<ResultType>) {
        if(result.value != newValue)
            result.value = newValue
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    // Called to get the cached data from the database.
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<Resource<RequestType>>

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected fun onFetchFailed(){}

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    fun getAsLiveData(): LiveData<Resource<ResultType>> = result
}