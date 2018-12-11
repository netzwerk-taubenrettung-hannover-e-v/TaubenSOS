package de.unihannover.se.tauben2

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import de.unihannover.se.tauben2.model.network.Resource
import de.unihannover.se.tauben2.viewmodel.BaseViewModel
import de.unihannover.se.tauben2.viewmodel.ViewModelFactory
import retrofit2.Response
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.design_layout_snackbar_include.view.*


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

fun <T: ViewModel> FragmentActivity.getViewModel(modelClass: Class<T>): T  = ViewModelProviders.of(this, ViewModelFactory(this)).get(modelClass)

fun <T: ViewModel> Fragment.getViewModel(modelClass: Class<T>): T? {
    this.context?.let {
        return ViewModelProviders.of(this, ViewModelFactory(it)).get(modelClass)
    }
    return null
}

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

fun setSnackBar(root: View, snackTitle: String) {
    val snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT)
    snackbar.show()
    val view = snackbar.view
    val txtv = view.snackbar_text
    txtv.gravity = Gravity.CENTER_HORIZONTAL
}

fun PopupWindow.dimBehind() {
    val container = contentView.rootView
    val context = contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = 0.3f
    wm.updateViewLayout(container, p)
}