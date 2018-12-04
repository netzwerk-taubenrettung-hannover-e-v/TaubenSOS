package de.unihannover.se.tauben2.view

import android.util.Log
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.model.network.Resource

//TODO implement loading indicator
class LoadingObserver<T>(val onSuccess: (T)-> Unit, val onLoading: ()->Unit = {}, val onError: (message: String?)->Unit = {}): Observer<Resource<T>> {

    constructor(successObserver: Observer<in T>, onLoading: ()->Unit = {}, onError: (message: String?)->Unit = {} ) : this({ data -> successObserver.onChanged(data) }, onLoading, onError)

    override fun onChanged(res: Resource<T>?) {
        when {
            res?.hasError() == true -> onError(res.message)
            res?.status?.isLoading() == true -> {
                Log.e("Tauben2", "I'm loading anything")
                onLoading()
                //TODO showing loading indicator
            }
            res?.status?.isSuccessful() == true && res.data != null -> //TODO hiding loading indicator
                onSuccess(res.data)
        }
    }
}