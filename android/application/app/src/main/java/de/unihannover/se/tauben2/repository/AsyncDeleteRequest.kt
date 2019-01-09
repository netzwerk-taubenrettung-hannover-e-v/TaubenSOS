package de.unihannover.se.tauben2.repository

import android.util.Log
import de.unihannover.se.tauben2.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * generic class for delete requests
 */
abstract class AsyncDeleteRequest<RequestType>(private val appExecutors: AppExecutors) {

    companion object {
        private val LOG_TAG = AsyncDeleteRequest::class.java.simpleName
    }

    fun send(data: RequestType) {
        val call = createCall(data)
        call.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(LOG_TAG, "Error deleting $data: ${t.message}")
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(LOG_TAG, "$data deleted successfully")
            }
        })
        appExecutors.diskIO().execute {
            deleteFromDB(data)
        }
    }

    protected abstract fun deleteFromDB(requestData: RequestType)

    protected abstract fun createCall(requestData: RequestType): Call<Void>
}