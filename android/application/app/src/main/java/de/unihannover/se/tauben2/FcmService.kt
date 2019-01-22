package de.unihannover.se.tauben2

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

        message?.data?.apply {
            get("case")?.let {
                val c = Gson().fromJson(it, Case::class.java)
                c?.let {case ->

                    if(database.caseDao().getCase(case.caseID ?: -1).value == null)
                        database.caseDao().insertOrUpdate(case)
                }
            }
            get("news")?.let {
                val n = Gson().fromJson(it, News::class.java)
                n?.let { news ->
                    if(database.newsDao().getNewsPost(news.feedID ?: -1).value == null)
                        database.newsDao().insertOrUpdate(news)
                }
            }
        }
    }

}