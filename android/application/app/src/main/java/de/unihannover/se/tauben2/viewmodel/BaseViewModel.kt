package de.unihannover.se.tauben2.viewmodel

import androidx.lifecycle.ViewModel
import android.content.Context
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.model.LocalDatabase
import de.unihannover.se.tauben2.repository.Repository

abstract class BaseViewModel(context: Context) : ViewModel(){

    protected val repository: Repository = Repository(LocalDatabase.getDatabase(context), App.getNetworkService())
}