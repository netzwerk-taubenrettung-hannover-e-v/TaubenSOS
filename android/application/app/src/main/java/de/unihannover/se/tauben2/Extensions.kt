package de.unihannover.se.tauben2

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import de.unihannover.se.tauben2.model.network.Resource
import de.unihannover.se.tauben2.viewmodel.BaseViewModel
import de.unihannover.se.tauben2.viewmodel.ViewModelFactory
import retrofit2.Response

fun <ResultType> Response<ResultType>.toResource(): Resource<ResultType> {
    val error = errorBody()?.toString() ?: message()
    return when {
        isSuccessful -> {
            val body = body()
            when {
                body != null -> Resource.success(body)
                else -> Resource.error(error)
            }
        }
        else -> Resource.error(error)
    }
}

fun <T: BaseViewModel> FragmentActivity.getViewModel(modelClass: Class<T>): T  = ViewModelProviders.of(this, ViewModelFactory(this)).get(modelClass)

fun <X> LiveDataRes<List<X>>.filter(func: (X) -> Boolean): LiveDataRes<List<X>> = Transformations.map(this) {
    var result: Resource<List<X>>? = null
    if(it != null){
        if(it.status.isSuccessful())
            result = Resource.success(it.data?.filter(func))
        if(it.status.isLoading())
            result = Resource.loading()
    } else result = Resource.error(it?.message)
    result
}