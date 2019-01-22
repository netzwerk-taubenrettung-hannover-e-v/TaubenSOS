package de.unihannover.se.tauben2

import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import de.unihannover.se.tauben2.model.database.LocalDatabase
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.repository.Repository
import de.unihannover.se.tauben2.view.main.BootingActivity

class FcmService : FirebaseMessagingService() {

    val database = LocalDatabase.getDatabase(App.context)
//    private val sp = getSharedPreferences("tauben2", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "FcmService"
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "InstanceID token has been updated to $token")
        val repository = Repository(database, App.getNetworkService())
        BootingActivity.owner?.let {
            it.registrationToken = token
            repository.updateUser(it)
        }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
//        val database = LocalDatabase.getDatabase(this)
        Log.i("Push", "Message received")
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        Log.i("Push", "SP loaded successfully")
        message?.data?.apply {
            get("case")?.let {
                val c = Gson().fromJson(it, Case::class.java)
                Log.i("Push", c.caseID.toString())
//                database.caseDao().insertOrUpdate(c)
                multiLet(c, sp.getString(Repository.LOGIN_USERNAME_KEY, null)){ case, username ->
                    if(username != case.reporter)
                        database.caseDao().insertOrUpdate(case)
                }
            }
            get("news")?.let {
                val n = Gson().fromJson(it, News::class.java)
//                database.newsDao().insertOrUpdate(n)
                multiLet(n, sp.getString(Repository.LOGIN_USERNAME_KEY, null)){ news, username ->
                    if(username != news.author)
                        database.newsDao().insertOrUpdate(news)
                }
            }
        }
    }

}