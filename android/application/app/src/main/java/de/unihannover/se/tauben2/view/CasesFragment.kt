package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.LimitedAccessible
import de.unihannover.se.tauben2.model.Permission
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.network.Resource
import kotlinx.android.synthetic.main.fragment_cases.view.*

class CasesFragment : Fragment(), Observer<Resource<List<Case>>> {

    companion object {
        fun newInstance(): CasesFragment {
            return CasesFragment()
        }
    }

    override fun onChanged(cases: Resource<List<Case>>?) {
        if(cases?.status?.isSuccessful() == true) {
            view?.textView?.text = cases.data?.get(0)?.additionalInfo ?:"Probleme beim Laden"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cases, container, false)
    }
}