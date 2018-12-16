package de.unihannover.se.tauben2

import android.content.Context
import android.content.pm.PackageManager
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import de.unihannover.se.tauben2.model.network.Resource
import de.unihannover.se.tauben2.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.design_layout_snackbar_include.view.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


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

fun setSnackBar(root: View, snackTitle: String, anchorView: View? = null) {
    val snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT)
    if(anchorView == null)
        snackbar.anchorView = root.rootView.bottom_navigation
    else
        snackbar.anchorView = anchorView
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

fun hasDevicePermission(context: Context, permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

private fun getDatePattern() = (DateFormat.getDateFormat(App.context) as SimpleDateFormat).toLocalizedPattern()
private fun getLongDatePattern() = (DateFormat.getLongDateFormat(App.context) as SimpleDateFormat).toLocalizedPattern()
private fun getTimePattern() = (DateFormat.getTimeFormat(App.context) as SimpleDateFormat).toLocalizedPattern()

fun getDateString(time: Long) = SimpleDateFormat(getDatePattern(), Locale.getDefault()).format(time)
fun getDateTimeString(time: Long) = SimpleDateFormat(getDatePattern() + ", " + getTimePattern(), Locale.getDefault()).format(time)

fun getLongDurationString(time: Long): String {
    val minutes = ((time / (1000 * 60)) % 60).toInt()
    val hours   = ((time / (1000 * 60 * 60)) % 24).toInt()
    val days   = (time / (1000 * 60 * 60 * 24)).toInt()
    return App.context.getString(R.string.long_duration, days, hours, minutes)
}

fun getShortDurationString(time: Long): String {
    val seconds = ((time / 1000) % 60).toInt()
    val minutes = ((time / (1000 * 60)) % 60).toInt()
    val hours   = ((time / (1000 * 60 * 60)) % 24).toInt()
    return App.context.getString(R.string.short_duration, hours, minutes, seconds)
}

fun getLowSpaceDurationString(time: Long): String {
    val seconds = ((System.currentTimeMillis() - time) / 1000).toInt()
    if(seconds < 60)
        return App.context.resources.getQuantityString(R.plurals.second, seconds, seconds)
    val minutes = (seconds / 60.0).toInt()
    if(minutes < 60)
        return App.context.resources.getQuantityString(R.plurals.minute, minutes, minutes)
    val hours   = (minutes / 60.0).toInt()
    if(hours < 24)
        return App.context.resources.getQuantityString(R.plurals.hour, hours, hours)
    val days   = (hours / 24.0).toInt()
    return App.context.resources.getQuantityString(R.plurals.day, days, days)
}

fun getDpValue(dpValue: Int): Int {
    val d = App.context.resources.displayMetrics.density
    return (dpValue * d).toInt()
}