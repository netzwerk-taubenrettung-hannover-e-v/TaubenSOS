package de.unihannover.se.tauben2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.unihannover.se.tauben2.model.network.Resource

typealias LiveDataRes<T> = LiveData<Resource<T>>

typealias MutableLiveDataRes<T> = MutableLiveData<Resource<T>>