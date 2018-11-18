package de.unihannover.se.tauben2.viewmodel

import androidx.lifecycle.ViewModel
import android.content.Context
import de.unihannover.se.tauben2.repository.Repository

class BaseViewModel(context: Context) : ViewModel(){

    protected val repository: Repository = Repository()
}