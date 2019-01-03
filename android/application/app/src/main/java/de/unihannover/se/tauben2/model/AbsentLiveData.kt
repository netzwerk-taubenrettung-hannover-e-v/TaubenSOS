package de.unihannover.se.tauben2.model

import androidx.lifecycle.LiveData

class AbsentLiveData<T: Any?> : LiveData<T>() {

    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> = AbsentLiveData()
    }
}