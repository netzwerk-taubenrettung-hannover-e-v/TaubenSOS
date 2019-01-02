package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CaseViewModel::class.java) -> CaseViewModel(context) as T
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(context) as T
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> NewsViewModel(context) as T
            modelClass.isAssignableFrom(LocationViewModel::class.java) -> LocationViewModel(context) as T
            modelClass.isAssignableFrom(MediaViewModel::class.java) -> MediaViewModel(context) as T
            modelClass.isAssignableFrom(PopulationMarkerViewModel::class.java) -> PopulationMarkerViewModel(context) as T
            else -> throw IllegalArgumentException(this::class.java.simpleName + " Unknown ViewModelClass to handle")
        }
    }

}