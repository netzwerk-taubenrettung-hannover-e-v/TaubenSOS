package de.unihannover.se.tauben2

import android.app.Application
import de.unihannover.se.tauben2.model.network.LiveDataCallAdapterFactory
import de.unihannover.se.tauben2.model.network.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://tauben2.herokuapp.com/api/"

class App: Application() {


    companion object {
        private lateinit var mNetworkService: NetworkService

        fun getNetworkService() = mNetworkService
    }

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory()).build()

        mNetworkService = retrofit.create(NetworkService::class.java)
    }
}