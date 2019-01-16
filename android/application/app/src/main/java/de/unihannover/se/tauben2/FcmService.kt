package de.unihannover.se.tauben2

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import de.unihannover.se.tauben2.model.database.LocalDatabase
import de.unihannover.se.tauben2.repository.Repository
import de.unihannover.se.tauben2.view.main.BootingActivity

class FcmService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FcmService"
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "InstanceID token has been updated to $token")
        val repository = Repository(LocalDatabase.getDatabase(App.context), App.getNetworkService())
        BootingActivity.owner?.let {
            it.registrationToken = token
            repository.updateUser(it)
        }
    }

}