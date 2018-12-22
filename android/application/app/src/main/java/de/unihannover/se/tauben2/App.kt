package de.unihannover.se.tauben2

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.ColorRes
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.model.network.LiveDataCallAdapterFactory
import de.unihannover.se.tauben2.model.network.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://tauben2.eu-central-1.elasticbeanstalk.com/api/"

class App : Application() {

    companion object {
        private lateinit var mNetworkService: NetworkService
        val mCurrentUser = User("Pascal", true, true, "", null)
        val CURRENT_PERMISSION = when {
            mCurrentUser.isAdmin -> Permission.ADMIN
            mCurrentUser.isActivated -> Permission.AUTHORISED
            else -> Permission.GUEST
        }

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun getNetworkService() = mNetworkService

        fun getColor(@ColorRes colorRes: Int) =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    context.getColor(colorRes)
                else
                    context.resources.getColor(colorRes)

    }

    override fun onCreate() {
        super.onCreate()

        context = this

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory()).build()

        mNetworkService = retrofit.create(NetworkService::class.java)
    }
}