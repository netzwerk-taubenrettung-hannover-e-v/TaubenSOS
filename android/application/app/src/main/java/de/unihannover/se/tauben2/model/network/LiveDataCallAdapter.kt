package de.unihannover.se.tauben2.model.network

import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.toResource
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Retrofit adapter that converts the API-Call into a LiveData of Resource.
 * @param R data type
 */
class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveDataRes<R>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveDataRes<R> {
        return object : LiveDataRes<R>() {
            var started = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {

                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(response.toResource())
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            postValue(Resource.error(throwable.message))
                        }
                    })
                }
            }
        }
    }
}
