package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.view.recycler.CasesRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.CaseViewModel

class CasesFragment : Fragment() {

    companion object : Singleton<CasesFragment>() {
        override fun newInstance()= CasesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_cases, container, false)


        val recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment) as CasesRecyclerFragment
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        getViewModel(CaseViewModel::class.java)?.cases?.let {
            val notClosed = it.filter { case -> !case.isClosed }
            notClosed.observe(this, LoadingObserver(recyclerFragment){ message ->
                Toast.makeText(this.context, "Couldn't load events: $message", Toast.LENGTH_LONG).show()
            })

            notClosed.observe(this, mapsFragment)
        }
//
//        val rv = view.recycler_view
//        layoutManager = LinearLayoutManager(context)
//        rv.layoutManager = layoutManager
//
//        adapter = AdapterList()
//        rv.adapter = adapter

        return view
    }


}