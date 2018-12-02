package de.unihannover.se.tauben2.view

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.view.recycler.CasesRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import de.unihannover.se.tauben2.R.drawable.ic_filter_list_white_24dp
import de.unihannover.se.tauben2.R.id.fab_action1
import kotlinx.android.synthetic.main.fragment_cases.*
import kotlinx.android.synthetic.main.fragment_cases.view.*


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

        initFab(view)

//        val rv = view.recycler_view
//        layoutManager = LinearLayoutManager(context)
//        rv.layoutManager = layoutManager
//
//        adapter = AdapterList()
//        rv.adapter = adapter

        return view
    }

    private fun initFab (view : View) {

        val speedDialView = view.speedDial

        // all cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_action1, R.drawable.ic_assignment_white_24dp)
                        .create()
        )
        // open cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_action2, R.drawable.ic_assignment_late_white_24dp)
                        .setLabelColor(Color.WHITE)
                        .create()
        )
        // closed cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_action3, R.drawable.ic_assignment_turned_in_white_24dp)
                        .setLabelColor(Color.WHITE)
                        .create()
        )
        // my cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_action4, R.drawable.ic_assignment_ind_white_24dp)
                        .setLabelColor(Color.WHITE)
                        .create()
        )

    }

}