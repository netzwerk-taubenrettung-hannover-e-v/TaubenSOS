package de.unihannover.se.tauben2

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FcmService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FcmService"
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "InstanceID token has been updated to $token")
    }

}