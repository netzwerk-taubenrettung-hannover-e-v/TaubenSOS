package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.Case

class CaseViewModel(context: Context) : BaseViewModel(context) {

    val cases: LiveDataRes<List<Case>> = repository.getCases()

    fun sendCase(case: Case, pictures: List<ByteArray>) = repository.sendCase(case, pictures)

    fun deleteCase(case: Case) = repository.deleteCase(case)

    fun updateCase(case: Case, mediaItems: List<ByteArray>) = repository.updateCase(case, mediaItems)
}