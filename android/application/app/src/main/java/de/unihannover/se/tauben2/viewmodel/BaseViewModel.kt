package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.model.database.LocalDatabase
import de.unihannover.se.tauben2.repository.Repository

abstract class BaseViewModel(context: Context) : ViewModel() {

    protected val repository: Repository = Repository(LocalDatabase.getDatabase(context), App.getNetworkService())
}