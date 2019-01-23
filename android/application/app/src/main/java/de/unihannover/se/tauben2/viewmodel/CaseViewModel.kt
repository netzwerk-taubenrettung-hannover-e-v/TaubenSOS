package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.network.Resource

class CaseViewModel(context: Context) : BaseViewModel(context) {
    val cases: LiveDataRes<List<Case>> = repository.getCases()

    fun getCase(caseId: Int) = repository.getCase(caseId)

    fun sendCase(case: Case, pictures: List<ByteArray>) = repository.sendCase(case, pictures)

    fun deleteCase(case: Case) = repository.deleteCase(case)

    fun updateCase(case: Case, mediaItems: List<ByteArray>) = repository.updateCase(case, mediaItems)

    fun reloadCasesFromServer(successFunction : () -> Any) {
        val result = repository.getCases(false)
        result.observeForever(object : Observer<Resource<List<Case>>> {
            override fun onChanged(t: Resource<List<Case>>?) {
                if(t?.status?.isSuccessful() == true) {
                    successFunction()
                    result.removeObserver(this)
                }
                if(t?.hasError() == true)
                    result.removeObserver(this)

            }

        })
    }

}