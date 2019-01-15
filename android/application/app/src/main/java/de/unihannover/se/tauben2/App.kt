package de.unihannover.se.tauben2

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.ColorRes
import de.unihannover.se.tauben2.model.network.LiveDataCallAdapterFactory
import de.unihannover.se.tauben2.model.network.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.OkHttp3Downloader



private const val BASE_AMAZON_URL = "https://tauben2.eu-central-1.elasticbeanstalk.com/api/"
private const val BASE_HEROKU_URL = "https://tauben2.herokuapp.com/api/"
private const val USE_AMAZON = false

class App : Application() {

    companion object {
        private lateinit var mNetworkService: NetworkService

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun getNetworkService() = mNetworkService

        fun getColor(@ColorRes colorRes: Int) =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    context.getColor(colorRes)
                else
                    context.resources.getColor(colorRes)

        fun getBaseURL() = if(USE_AMAZON) BASE_AMAZON_URL else BASE_HEROKU_URL

    }

    override fun onCreate() {
        super.onCreate()

        val picassoBuilder = Picasso.Builder(this)
        picassoBuilder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        Picasso.setSingletonInstance(picassoBuilder.build())

        context = this

        val retrofit = Retrofit.Builder().baseUrl(getBaseURL())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory()).build()

        mNetworkService = retrofit.create(NetworkService::class.java)
    }
}