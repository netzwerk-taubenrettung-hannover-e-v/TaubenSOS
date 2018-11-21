package de.unihannover.se.tauben2.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val fragmentActivity: FragmentActivity): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CaseViewModel::class.java) -> CaseViewModel(fragmentActivity) as T
            else -> throw IllegalArgumentException(this::class.java.simpleName + " Unknown ViewModelClass to handle")
        }
    }

}