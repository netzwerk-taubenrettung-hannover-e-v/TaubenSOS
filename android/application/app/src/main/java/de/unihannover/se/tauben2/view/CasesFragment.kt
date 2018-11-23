package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.LimitedAccessible
import de.unihannover.se.tauben2.model.Permission
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.network.Resource

class CasesFragment : Fragment(), Observer<Resource<List<Case>>> {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<AdapterList.ViewHolder>? = null

    companion object {
        fun newInstance(): CasesFragment {
            return CasesFragment()
        }
    }

    override fun onChanged(cases: Resource<List<Case>>?) {
        if(cases?.status?.isSuccessful() == true) {
            // view?.textView?.text = cases.data?.get(0)?.additionalInfo ?:"Probleme beim Laden"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_cases, container, false)

        var rv = view.findViewById(R.id.recycler_view) as RecyclerView
        layoutManager = LinearLayoutManager(activity)
        rv.layoutManager = layoutManager

        adapter = AdapterList()
        rv.adapter = adapter


        return view
    }
}