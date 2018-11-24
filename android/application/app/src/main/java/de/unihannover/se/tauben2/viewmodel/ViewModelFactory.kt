package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val context: Context): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CaseViewModel::class.java) -> CaseViewModel(context) as T
            modelClass.isAssignableFrom(LocationViewModel::class.java) -> LocationViewModel(context) as T
            else -> throw IllegalArgumentException(this::class.java.simpleName + " Unknown ViewModelClass to handle")
        }
    }

}