package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.entity.Case

class CaseViewModel(context: Context): BaseViewModel(context) {

    val cases: LiveDataRes<List<Case>> = repository.getCases()

    fun sendCase(case: Case) = repository.sendCase(case)
}